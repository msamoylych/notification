package org.java.netty.client.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.*;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.PlatformDependent;
import org.java.notification.Message;
import org.java.utils.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by msamoylych on 04.04.2017.
 */
class NettyHttp2ClientHandler<M extends Message> extends Http2ConnectionHandler {

    private final AtomicInteger streamIds = new AtomicInteger(1);

    private final NettyHttp2ClientAdapter<M> adapter;
    private final Map<Integer, Stream> streams;

    static class Builder<M extends Message> extends AbstractHttp2ConnectionHandlerBuilder<NettyHttp2ClientHandler<M>, Builder<M>> {

        private NettyHttp2ClientAdapter<M> adapter;

        Builder() {
            server(false);
        }

        @Override
        protected Builder<M> frameLogger(Http2FrameLogger frameLogger) {
            return super.frameLogger(frameLogger);
        }

        Builder<M> adapter(NettyHttp2ClientAdapter<M> adapter) {
            this.adapter = adapter;
            return this;
        }

        @Override
        protected NettyHttp2ClientHandler<M> build() {
            return super.build();
        }

        @Override
        protected NettyHttp2ClientHandler<M> build(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder, Http2Settings initialSettings) throws Exception {
            NettyHttp2ClientHandler<M> handler = new NettyHttp2ClientHandler<>(decoder, encoder, initialSettings, adapter);
            frameListener(handler.new NettyHttp2ClientFrameListener());
            return handler;
        }
    }

    private NettyHttp2ClientHandler(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder, Http2Settings initialSettings,
                                    NettyHttp2ClientAdapter<M> adapter) {
        super(decoder, encoder, initialSettings);
        this.adapter = adapter;
        streams = PlatformDependent.newConcurrentHashMap();
    }

    private class NettyHttp2ClientFrameListener extends Http2FrameAdapter {

        @Override
        public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endOfStream) throws Http2Exception {
            onHeadersRead(ctx, streamId, headers, padding, endOfStream);
        }

        @Override
        public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endOfStream) throws Http2Exception {
            Stream stream;
            if (endOfStream) {
                stream = streams.remove(streamId);
                adapter.handleResponse(stream.message, headers, null);
            } else {
                stream = streams.get(streamId);
                stream.headers = headers;
            }
        }

        @Override
        public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream) throws Http2Exception {
            int bytesProcessed = data.readableBytes() + padding;

            String response = data.toString(StandardCharsets.UTF_8);

            if (endOfStream) {
                Stream stream = streams.remove(streamId);
                adapter.handleResponse(stream.message, stream.headers, response);
            }

            return bytesProcessed;
        }

        @Override
        public void onGoAwayRead(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData) throws Http2Exception {
            ctx.close();
        }
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Http2Exception {
        M message = (M) msg;

        int streamId = connection().local().incrementAndGetNextStreamId();

        Http2Headers headers = adapter.headers(message);
        ChannelFuture headersFuture = encoder().writeHeaders(ctx, streamId, headers, 0, false, ctx.newPromise());

        byte[] content = StringUtils.getBytes(adapter.content(message));
        ByteBuf data = ctx.alloc().buffer(content.length);
        data.writeBytes(content);
        ChannelFuture dataFuture = encoder().writeData(ctx, streamId, data, 0, true, ctx.newPromise());

        PromiseCombiner promiseCombiner = new PromiseCombiner();
        promiseCombiner.addAll(headersFuture, dataFuture);
        promiseCombiner.finish(promise);

        promise.addListener(future -> {
            if (future.isSuccess()) {
                streams.put(streamId, new Stream(message));
            } else {
                adapter.fail(message, future.cause());
            }
        });
    }

    private class Stream {
        private M message;
        private Http2Headers headers;

        private Stream(M message) {
            this.message = message;
        }
    }
}
