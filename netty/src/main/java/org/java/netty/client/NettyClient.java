package org.java.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.java.netty.Netty;
import org.java.notification.Message;
import org.java.notification.client.Client;
import org.java.notification.client.ClientAdapter;
import org.java.notification.client.SendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by msamoylych on 04.04.2017.
 */
public abstract class NettyClient<M extends Message> implements Client<M> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    protected final ClientAdapter adapter;
    protected final Bootstrap bootstrap;

    protected volatile Channel channel;

    public NettyClient(ClientAdapter adapter) {
        this.adapter = adapter;

        LOGGER.info("Init <{}> ({}:{})", adapter.getClass().getSimpleName(), adapter.host(), adapter.port());

        bootstrap = new Bootstrap()
                .group(Netty.CLIENT())
                .remoteAddress(adapter.host(), adapter.port())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public void send(M msg) throws SendException {
        try {
            channel().writeAndFlush(msg).addListener(future -> {
                if (future.cause() != null) {
                    LOGGER.error("Send error ({})", adapter.getClass().getSimpleName(), future.cause());
                }
            });
        } catch (Throwable th) {
            LOGGER.error("Send error ({})", adapter.getClass().getSimpleName(), th);
            throw new SendException();
        }
    }

    private Channel channel() {
        final Channel channel = this.channel;
        return channel != null ? channel : connect();
    }

    protected abstract Channel connect();
}
