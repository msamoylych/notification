package org.java.netty.client.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import org.java.netty.NettyException;
import org.java.netty.SslContextFactory;
import org.java.netty.client.NettyClient;
import org.java.notification.Message;
import org.java.notification.client.http.HttpClientAdapter;

import java.util.concurrent.TimeUnit;

/**
 * Created by msamoylych on 06.04.2017.
 */
public class NettyHttpClient<M extends Message> extends NettyClient<M> {

    public NettyHttpClient(HttpClientAdapter<M> adapter) {
        super(adapter);

        SslContext sslContext = SslContextFactory.buildSSLContext();

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast(sslContext.newHandler(ch.alloc()));

                pipeline.addLast(new IdleStateHandler(0, 0, 60_000, TimeUnit.MILLISECONDS));

                pipeline.addLast(NettyHttpClientHandler.handlers(adapter));
            }
        });
    }

    @Override
    protected Channel connect() {
        synchronized (bootstrap) {
            if (channel != null) {
                return channel;
            }

            LOGGER.info("Connecting ({})", adapter.getClass().getSimpleName());
            ChannelFuture connect = bootstrap.connect();
            if (connect.awaitUninterruptibly(5_000, TimeUnit.MILLISECONDS) && connect.isSuccess()) {
                LOGGER.info("Connected ({})", adapter.getClass().getSimpleName());

                final Channel ch = connect.channel();
                channel = ch;
                return ch;
            } else {
                if (connect.cause() != null) {
                    throw new NettyException("Connection error", connect.cause());
                } else {
                    throw new NettyException("Connection timeout");
                }
            }
        }
    }
}
