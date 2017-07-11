package org.java.netty.client.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
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

        SslContext sslContext = SslContextFactory.buildHttpSslContext();

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast(new WriteTimeoutHandler(1_000, TimeUnit.MILLISECONDS));

                pipeline.addLast(sslContext.newHandler(ch.alloc()));

                pipeline.addLast(new IdleStateHandler(0, 0, 60_000, TimeUnit.MILLISECONDS));

                pipeline.addLast(NettyHttpClientHandler.handlers(adapter));
            }
        });
    }

    @Override
    protected Channel connect() throws NettyException {
        synchronized (bootstrap) {
            if (channel != null) {
                return channel;
            }

            ChannelFuture connect = bootstrap.connect();
            if (connect.awaitUninterruptibly(5_000, TimeUnit.MILLISECONDS) && connect.isSuccess()) {
                Channel ch = connect.channel();
                ch.closeFuture().addListener(future -> {
                    synchronized (bootstrap) {
                        channel = null;
                        LOGGER.info("{} - disconnected", adapter);
                    }
                });
                channel = ch;
                LOGGER.info("{} - connected", adapter);
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
