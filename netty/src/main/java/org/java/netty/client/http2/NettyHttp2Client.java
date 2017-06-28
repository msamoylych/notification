package org.java.netty.client.http2;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.java.netty.NettyException;
import org.java.netty.SslContextFactory;
import org.java.netty.client.NettyClient;
import org.java.notification.Message;
import org.java.notification.client.http.HttpClientAdapter;

import java.util.concurrent.TimeUnit;

/**
 * Created by msamoylych on 04.04.2017.
 */
public class NettyHttp2Client<M extends Message> extends NettyClient<M> {
    private static final Http2FrameLogger FRAME_LOGGER = new Http2FrameLogger(LogLevel.INFO);

    private ChannelPromise configure;

    public NettyHttp2Client(HttpClientAdapter<M> adapter) {
        super(adapter);

        SslContext sslContext = SslContextFactory.buildHttp2SslContext();

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast(new WriteTimeoutHandler(1_000, TimeUnit.MILLISECONDS));

                pipeline.addLast(sslContext.newHandler(ch.alloc()));

                configure = ch.newPromise();

                pipeline.addLast(new ApplicationProtocolNegotiationHandler("") {
                    @Override
                    protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
                        if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
                            ChannelPipeline channelPipeline = ctx.pipeline();

                            NettyHttp2ClientHandler<M> connectionHandler = new NettyHttp2ClientHandler.Builder<M>()
                                    .frameLogger(FRAME_LOGGER)
                                    .adapter(new NettyHttp2ClientAdapter<>(adapter))
                                    .build();

                            channelPipeline.addLast(connectionHandler);
                            configure.setSuccess();
                        } else {
                            ctx.close();
                        }
                    }
                });
            }
        });
    }

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

                if (configure.awaitUninterruptibly(5_000, TimeUnit.MILLISECONDS)) {
                    ch.closeFuture().addListener(future -> {
                        synchronized (bootstrap) {
                            channel = null;
                        }
                    });
                    channel = ch;
                    return ch;
                } else {
                    ch.close();
                    throw new NettyException("Wait setting timeout");
                }
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
