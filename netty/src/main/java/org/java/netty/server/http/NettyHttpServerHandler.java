package org.java.netty.server.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.java.notification.server.http.HttpServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by msamoylych on 17.04.2017.
 */
class NettyHttpServerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpServerHandler.class);

    private final NettyHttpServerAdapter adapter;
    private final InboundHandler inboundHandler;

    private NettyHttpServerHandler(HttpServerHandler handler) {
        this.adapter = new NettyHttpServerAdapter(handler);
        inboundHandler = new InboundHandler();
    }

    private class InboundHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
            adapter.adapt(ctx, msg);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                ctx.close();
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            LOGGER.error(cause.getMessage(), cause);
            ctx.close();
        }
    }

    private static class Codec extends CombinedChannelDuplexHandler<HttpRequestDecoder, HttpResponseEncoder> {
        Codec() {
            init(new HttpRequestDecoder(4096, 8192, 8192, true, 128), new HttpResponseEncoder());
        }
    }

    static ChannelHandler[] handlers(HttpServerHandler adapter) {
        NettyHttpServerHandler handler = new NettyHttpServerHandler(adapter);
        return new ChannelHandler[]{
                new Codec(),
                new HttpServerKeepAliveHandler(),
                new HttpObjectAggregator(65536),
                new ChunkedWriteHandler(),
                new IdleStateHandler(0, 0, 5),
                handler.inboundHandler
        };
    }
}
