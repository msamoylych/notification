package org.java.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import org.java.utils.lifecycle.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * Created by msamoylych on 04.04.2017.
 */
@Component
public class Netty extends SmartLifecycle {

    private EventLoopGroup acceptor;
    private EventLoopGroup server;
    private EventLoopGroup client;
    private EventExecutorGroup executor;

    @Override
    protected void doStart() {
        acceptor = new NioEventLoopGroup(0, new ThreadFactory("acceptor"));
        server = new NioEventLoopGroup(0, new ThreadFactory("server"));
        client = new NioEventLoopGroup(0, new ThreadFactory("client"));
        executor = new DefaultEventExecutorGroup(100, new ThreadFactory("executor"));
    }

    @Override
    protected void doStop() {
        executor.shutdownGracefully();
        acceptor.shutdownGracefully();
        server.shutdownGracefully();
        client.shutdownGracefully();
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