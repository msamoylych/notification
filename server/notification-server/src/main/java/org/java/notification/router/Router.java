package org.java.notification.router;

import org.java.notification.client.SendException;
import org.java.notification.push.Push;
import org.java.notification.push.application.Application;
import org.java.notification.sender.Sender;
import org.java.utils.BeanUtils;
import org.java.utils.GenericUtils;
import org.java.utils.lifecycle.SmartLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by msamoylych on 30.05.2017.
 */
@Component
public class Router extends SmartLifecycle implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(Router.class);

    private final Map<Class<? extends Application>, Sender> pushSenders = new HashMap<>();
    private ThreadPoolExecutor executor;

    public void route(List<Push<?>> pushes) {
        pushes.forEach(push -> executor.submit(new PushRouterTask(push)));
    }

    @Override
    protected void doStart() throws Exception {
        executor = new ThreadPoolExecutor(1, 8, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100000),
                new RouterThreadFactory(), new RejectedHandler());
    }

    @Override
    protected void doStop() throws Exception {
        executor.shutdown();
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            LOGGER.error("Await termination failed");
        }
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @SuppressWarnings("unchecked")
    public void setApplicationContext(ApplicationContext applicationContext) {
        BeanUtils.forEachBeanOfType(applicationContext, Sender.class, sender -> {
            ParameterizedType senderType = (ParameterizedType) GenericUtils.getGenericType(sender);
            if (senderType.getRawType() == Push.class) {
                Type applicationType = GenericUtils.getGenericType(senderType);
                pushSenders.put((Class<? extends Application>) applicationType, sender);
            }
        });
    }

    private static class RouterThreadFactory implements ThreadFactory {
        private static final AtomicInteger num = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "router-" + num.getAndIncrement());
        }
    }

    private class RejectedHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        }
    }

    private class PushRouterTask implements Runnable {

        private final Push<?> push;

        PushRouterTask(Push<?> push) {
            this.push = push;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            Class<? extends Application> cls = push.application().getClass();
            Sender sender = pushSenders.get(cls);
            if (sender != null) {
                try {
                    sender.send(push);
                } catch (SendException ex) {
                    LOGGER.error("Push <{}> send failed", push.id(), ex);
                }
            } else {
                LOGGER.error("Sender for {} not found");
            }
        }
    }
}
