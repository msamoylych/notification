package org.java.notification.router;

import org.java.notification.push.Push;
import org.java.utils.lifecycle.SmartLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by msamoylych on 30.05.2017.
 */
@Component
public class Router extends SmartLifecycle {
    private static final Logger LOGGER = LoggerFactory.getLogger(Router.class);

    private ThreadPoolExecutor executor;

    public void route(List<Push<?>> pushes) {
        pushes.forEach(push -> executor.submit(new PushRouterTask(push)));
    }

    @Override
    protected void doStart() throws Exception {
        executor = new ThreadPoolExecutor(1, 8, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100000), new RouterThreadFactory());
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

    private static class RouterThreadFactory implements ThreadFactory {
        private static final AtomicInteger num = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "router-" + num.getAndIncrement());
        }
    }
}
