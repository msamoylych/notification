package org.java.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.java.netty.Netty;
import org.java.notification.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by msamoylych on 17.04.2017.
 */
public abstract class NettyServer implements Server {
    protected static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);
    private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);

    protected final String name;
    protected final ServerBootstrap bootstrap;

    private ChannelFuture channelFuture;

    public NettyServer(Netty netty, String name, int port) {
        this.name = name;

        LOGGER.info("Init <{}> ({})", name, port);

        bootstrap = new ServerBootstrap()
                .group(netty.acceptor(), netty.server())
                .localAddress(port)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .handler(LOGGING_HANDLER);
    }

    @Override
    public void start() {
        synchronized (bootstrap) {
            LOGGER.info("Start ({})", name);
            channelFuture = bootstrap.bind().addListener((ChannelFutureListener) cf -> {
                if (cf.isSuccess()) {
                    LOGGER.info("Started ({})", name);
                } else {
                    LOGGER.error("Start error ({})", name, cf.cause());
                }
            });
        }
    }

    @Override
    public void stop() {
        synchronized (bootstrap) {
            LOGGER.info("Stop ({})", name);
            channelFuture.channel().close().addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    LOGGER.info("Stopped ({})", name);
                } else {
                    LOGGER.error("Stop error ({})", name, channelFuture.cause());
                }
            });
        }
    }
}
