package org.java.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import org.java.utils.lifecycle.SmartLifecycle;
import org.java.utils.settings.SettingsRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by msamoylych on 04.04.2017.
 */
@Component
public class Netty extends SmartLifecycle implements SettingsRegister {
    private static final Logger LOGGER = LoggerFactory.getLogger(Netty.class);

    private final int acceptorThreadCount = setting("ACCEPTOR_THREAD_COUNT", "Количество потоков для обработки входящих соединений", 0);
    private final int serverThreadCount = setting("SERVER_THREAD_COUNT", "Количество потоков для обработки входящих соединений", 0);
    private final int clientThreadCount = setting("CLIENT_THREAD_COUNT", "Количество потоков для обработки входящих соединений", 0);
    private final int executorThreadCount = setting("EXECUTOR_THREAD_COUNT", "Количество потоков для обработки входящих соединений", 16);

    private final EventLoopGroup acceptor = new NioEventLoopGroup(acceptorThreadCount, new ThreadFactory("acceptor"));
    private final EventLoopGroup server = new NioEventLoopGroup(serverThreadCount, new ThreadFactory("server"));
    private final EventLoopGroup client = new NioEventLoopGroup(clientThreadCount, new ThreadFactory("client"));
    private final EventExecutorGroup executor = new DefaultEventExecutorGroup(executorThreadCount, new ThreadFactory("executor"));

    @Override
    protected void doStart() throws Exception {
    }

    @Override
    protected void doStop() throws Exception {
        Future<?> executorShutdown = executor.shutdownGracefully();
        if (!executorShutdown.await(5, TimeUnit.SECONDS)) {
            LOGGER.error("Await executor shutdown failed");
        }
        Future<?> acceptorShutdown = acceptor.shutdownGracefully();
        if (!acceptorShutdown.await(5, TimeUnit.SECONDS)) {
            LOGGER.error("Await acceptor shutdown failed");
        }
        Future<?> serverShutdown = server.shutdownGracefully();
        if (!serverShutdown.await(5, TimeUnit.SECONDS)) {
            LOGGER.error("Await server shutdown failed");
        }
        Future<?> clientShutdown = client.shutdownGracefully();
        if (!clientShutdown.await(5, TimeUnit.SECONDS)) {
            LOGGER.error("Await client shutdown failed");
        }
    }

    @Override
    public int getPhase() {
        return 0;
    }

    public EventLoopGroup acceptor() {
        return acceptor;
    }

    public EventLoopGroup server() {
        return server;
    }

    public EventLoopGroup client() {
        return client;
    }

    public EventExecutorGroup executor() {
        return executor;
    }

    private static class ThreadFactory extends DefaultThreadFactory {
        private ThreadFactory(String poolName) {
            super(poolName);
        }

        @Override
        protected Thread newThread(Runnable r, String name) {
            return super.newThread(r, name.replaceAll("-\\d-", "-"));
        }
    }
}