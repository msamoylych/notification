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

    private static EventLoopGroup ACCEPTOR;
    private static EventLoopGroup SERVER;
    private static EventLoopGroup CLIENT;

    @Override
    protected void doStart() {
        ACCEPTOR = new NioEventLoopGroup(0, new ThreadFactory("acceptor"));
        SERVER = new NioEventLoopGroup(0, new ThreadFactory("server"));
        CLIENT = new NioEventLoopGroup(0, new ThreadFactory("client"));
    }

    @Override
    protected void doStop() {
        ACCEPTOR.shutdownGracefully();
        SERVER.shutdownGracefully();
        CLIENT.shutdownGracefully();
    }

    @Override
    public int getPhase() {
        return 0;
    }

    public static EventLoopGroup ACCEPTOR() {
        return ACCEPTOR;
    }

    public static EventLoopGroup SERVER() {
        return SERVER;
    }

    public static EventLoopGroup CLIENT() {
        return CLIENT;
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