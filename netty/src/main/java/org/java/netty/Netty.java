package org.java.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
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

    @Override
    protected void doStart() {
        acceptor = new NioEventLoopGroup(0, new ThreadFactory("acceptor"));
        server = new NioEventLoopGroup(0, new ThreadFactory("server"));
        client = new NioEventLoopGroup(0, new ThreadFactory("client"));
    }

    @Override
    protected void doStop() {
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