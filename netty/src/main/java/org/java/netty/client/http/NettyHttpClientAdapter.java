package org.java.netty.client.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.java.notification.Message;
import org.java.notification.client.http.HttpClientAdapter;
import org.java.notification.client.http.HttpClientAdapter.Headers;
import org.java.utils.http.Header;

import java.nio.charset.StandardCharsets;

/**
 * Created by msamoylych on 07.04.2017.
 */
class NettyHttpClientAdapter<M extends Message> {

    private final HttpClientAdapter<M> adapter;

    NettyHttpClientAdapter(HttpClientAdapter<M> adapter) {
        this.adapter = adapter;
    }

    HttpRequest adapt(ChannelHandlerContext ctx, M msg) {
        byte[] payload = adapter.content(msg).getBytes(StandardCharsets.UTF_8);
        ByteBuf content = ctx.alloc().ioBuffer(payload.length);
        content.writeBytes(payload);

        HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method(), adapter.path(), content);

        HttpHeaders headers = request.headers();
        adapter.headers(new NettyHttpClientHeaders(headers), msg);
        headers.add(Header.CONTENT_LENGTH, content.capacity());

        return request;
    }

    private HttpMethod method() {
        switch (adapter.method()) {
            case GET:
                return HttpMethod.GET;
            case POST:
                return HttpMethod.POST;
            default:
                throw new IllegalStateException("unknown method: " + adapter.method());
        }
    }

    private static class NettyHttpClientHeaders implements Headers {
        private HttpHeaders headers;

        private NettyHttpClientHeaders(HttpHeaders headers) {
            this.headers = headers;
        }

        @Override
        public void set(String name, String value) {
            headers.add(name, value);
        }

        @Override
        public String get(String name) {
            return headers.get(name);
        }
    }
}
