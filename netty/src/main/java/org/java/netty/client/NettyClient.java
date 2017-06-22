package org.java.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.java.netty.Netty;
import org.java.notification.Message;
import org.java.notification.client.Client;
import org.java.notification.client.SendException;
import org.java.notification.client.http.HttpClientAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by msamoylych on 04.04.2017.
 */
public abstract class NettyClient<M extends Message> implements Client<M> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    protected final String name;
    protected final Bootstrap bootstrap;

    protected volatile Channel channel;

    public NettyClient(HttpClientAdapter<M> adapter) {
        this.name = adapter.name();

        String host = adapter.host();
        int port = adapter.port();

        LOGGER.info("Init <{}> ({}:{})", name, host, port);

        bootstrap = new Bootstrap()
                .group(Netty.CLIENT())
                .remoteAddress(host, port)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public void send(M msg) throws SendException {
        try {
            channel().writeAndFlush(msg).addListener(future -> {
                if (future.cause() != null) {
                    LOGGER.error("Send error ({})", name, future.cause());
                }
            });
        } catch (Throwable th) {
            LOGGER.error("Send error ({})", name, th);
            throw new SendException();
        }
    }

    private Channel channel() {
        final Channel channel = this.channel;
        return channel != null ? channel : connect();
    }

    protected abstract Channel connect();
}
