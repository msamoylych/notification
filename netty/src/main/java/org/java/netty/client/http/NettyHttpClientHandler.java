package org.java.netty.client.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.*;
import org.java.notification.Message;
import org.java.notification.client.http.HttpClientAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/**
 * Created by msamoylych on 06.04.2017.
 */
class NettyHttpClientHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpClientHandler.class);

    private final Queue<QueueMessage> messages = new ArrayDeque<>();
    private final NettyHttpClientAdapter<Message> adapter;
    private final OutboundHandler outboundHandler;
    private final InboundHandler inboundHandler;
    private final Codec codec;

    private NettyHttpClientHandler(HttpClientAdapter<Message> adapter) {
        this.adapter = new NettyHttpClientAdapter<>(adapter);
        outboundHandler = new OutboundHandler();
        inboundHandler = new InboundHandler();
        codec = new Codec();
    }

    private class OutboundHandler extends MessageToMessageEncoder<Message> {

        @Override
        protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
            messages.offer(new QueueMessage(msg));

            out.add(adapter.adapt(ctx, msg));
        }
    }

    private class InboundHandler extends SimpleChannelInboundHandler<HttpObject> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
            if (msg instanceof HttpResponse) {
                LOGGER.info(msg.toString());
            }
            if (msg instanceof HttpContent) {
                HttpContent content = (HttpContent) msg;
                LOGGER.info(content.content().toString(StandardCharsets.UTF_8));
            }
        }
    }

    private static class Codec extends CombinedChannelDuplexHandler<HttpResponseDecoder, HttpRequestEncoder> {
        Codec() {
            init(new HttpResponseDecoder(4096, 8192, 8192, true, 128), new HttpRequestEncoder());
        }
    }

    private static class QueueMessage {
        private Object msg;
        private HttpResponse response;

        private QueueMessage(Object msg) {
            this.msg = msg;
        }
    }

    static ChannelHandler[] handlers(HttpClientAdapter adapter) {
        NettyHttpClientHandler handler = new NettyHttpClientHandler(adapter);
        return new ChannelHandler[]{handler.codec, handler.outboundHandler, handler.inboundHandler};
    }
}
