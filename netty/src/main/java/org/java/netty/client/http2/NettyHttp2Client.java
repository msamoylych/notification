package org.java.netty.client.http2;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.java.netty.Netty;
import org.java.netty.NettyException;
import org.java.netty.SslContextFactory;
import org.java.netty.client.NettyClient;
import org.java.notification.Message;
import org.java.notification.client.http.Http2ClientAdapter;

import java.util.concurrent.TimeUnit;

/**
 * Created by msamoylych on 04.04.2017.
 */
public class NettyHttp2Client<M extends Message> extends NettyClient<M> {
    private static final Http2FrameLogger FRAME_LOGGER = new Http2FrameLogger(LogLevel.DEBUG);

    private ChannelPromise negotiation;

    public NettyHttp2Client(Netty netty, Http2ClientAdapter<M> clientAdapter) {
        super(netty, clientAdapter);

        SslContext sslContext = SslContextFactory.buildHttp2SslContext();

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                negotiation = ch.newPromise();

                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast(new WriteTimeoutHandler(1_000, TimeUnit.MILLISECONDS));

                pipeline.addLast(sslContext.newHandler(ch.alloc()));

                pipeline.addLast(new ApplicationProtocolNegotiationHandler("") {
                    @Override
                    protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
                        if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
                            ChannelPipeline channelPipeline = ctx.pipeline();

                            channelPipeline.addLast(new IdleStateHandler(0, 0, 60_000, TimeUnit.MILLISECONDS));

                            NettyHttp2ClientHandler<M> connectionHandler = new NettyHttp2ClientHandler.Builder<M>()
                                    .frameLogger(FRAME_LOGGER)
                                    .adapter(new NettyHttp2ClientAdapter<>((Http2ClientAdapter<M>) adapter))
                                    .build();

                            channelPipeline.addLast(connectionHandler);

                            negotiation.setSuccess();
                        } else {
                            negotiation.setFailure(new IllegalStateException("Unsupported protocol: " + protocol));
                        }
                    }

                    @Override
                    protected void handshakeFailure(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        negotiation.setFailure(cause);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        negotiation.setFailure(cause);
                    }
                });
            }
        });
    }

    protected Channel connect() throws NettyException {
        synchronized (bootstrap) {
            if (channel != null) {
                return channel;
            }

            ChannelFuture connect = bootstrap.connect();
            if (connect.awaitUninterruptibly(5_000, TimeUnit.MILLISECONDS) && connect.isSuccess()) {
                Channel ch = connect.channel();

                if (negotiation.awaitUninterruptibly(5_000, TimeUnit.MILLISECONDS) && negotiation.isSuccess()) {
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
                    ch.close();
                    if (negotiation.cause() != null) {
                        throw new NettyException("Negotiation error", negotiation.cause());
                    } else {
                        throw new NettyException("Negotiation timeout");
                    }
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
