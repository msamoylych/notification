package org.java.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.java.netty.Netty;
import org.java.notification.Message;
import org.java.notification.client.Client;
import org.java.notification.client.ClientAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Created by msamoylych on 04.04.2017.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class NettyClient<M extends Message> implements Client<M> {
    protected final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    @Autowired
    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    private Netty netty;

    protected final ClientAdapter<M> adapter;
    protected final Bootstrap bootstrap;

    protected volatile Channel channel;

    public NettyClient(ClientAdapter<M> adapter) {
        this.adapter = adapter;

        bootstrap = new Bootstrap()
                .group(netty.client())
                .remoteAddress(adapter.host(), adapter.port())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public void send(M msg) {
        channel().writeAndFlush(msg).addListener(future -> {
            if (future.isSuccess()) {
                LOGGER.info("{} - sent", msg);
            } else {
                adapter.fail(msg, future.cause());
            }
        });
    }

    private Channel channel() {
        final Channel channel = this.channel;
        return channel != null ? channel : connect();
    }

    protected abstract Channel connect();
}
