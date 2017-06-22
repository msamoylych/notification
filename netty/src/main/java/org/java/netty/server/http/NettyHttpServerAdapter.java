package org.java.netty.server.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.stream.ChunkedInput;
import io.netty.handler.stream.ChunkedStream;
import org.java.notification.server.http.HttpResponse;
import org.java.notification.server.http.HttpServerHandler;
import org.java.utils.http.Method;
import org.java.utils.http.Status;
import org.java.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Objects;
import java.util.Set;

/**
 * Created by msamoylych on 20.04.2017.
 */
class NettyHttpServerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpServerAdapter.class);

    private static final FullHttpResponse BAD_REQUEST =
            new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.EMPTY_BUFFER);

    private static final FullHttpResponse SERVER_ERROR =
            new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.EMPTY_BUFFER);

    private final HttpServerHandler handler;

    NettyHttpServerAdapter(HttpServerHandler handler) {
        this.handler = handler;
    }

    void adapt(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (req.decoderResult().isFailure()) {
            sendError(ctx, BAD_REQUEST);
            return;
        }

        try {
            NettyHttpServerRequest request = new NettyHttpServerRequest(req);
            NettyHttpServerResponse response = new NettyHttpServerResponse();
            response.headers.set(HttpHeaderNames.SERVER, "Netty");
            handler.handle(request, response);

            InputStream is = response.content();
            if (is != null) {
                int length = is.available();
                if (length > 8192) {
                    DefaultHttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, response.status(), response.headers());
                    HttpUtil.setTransferEncodingChunked(res, true);
                    ctx.write(res);
                    ChunkedInput chunkedInput = new HttpChunkedInput(new ChunkedStream(is, 8192));
                    ctx.writeAndFlush(chunkedInput);
                } else {
                    ByteBuf content = ctx.alloc().buffer(length);
                    content.writeBytes(is, length);
                    IOUtils.closeQuietly(is);
                    FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, response.status(),
                            content, response.headers(), EmptyHttpHeaders.INSTANCE);
                    HttpUtil.setContentLength(res, length);
                    ctx.writeAndFlush(res);
                }
            } else {
                FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, response.status(),
                        Unpooled.EMPTY_BUFFER, response.headers(), EmptyHttpHeaders.INSTANCE);
                HttpUtil.setContentLength(res, 0);
                ctx.writeAndFlush(res);
            }
        } catch (Throwable th) {
            LOGGER.error(th.getMessage(), th);
            sendError(ctx, SERVER_ERROR);
        }
    }

    private static void sendError(ChannelHandlerContext ctx, FullHttpResponse response) {
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static class NettyHttpServerRequest implements org.java.notification.server.http.HttpRequest {
        private final FullHttpRequest request;

        private NettyHttpServerRequest(FullHttpRequest request) {
            this.request = request;
        }

        @Override
        public Method method() {
            HttpMethod method = request.method();
            if (method == HttpMethod.GET) {
                return Method.GET;
            } else if (method == HttpMethod.POST) {
                return Method.POST;
            } else {
                throw new IllegalStateException("Unknown method: " + method);
            }
        }

        @Override
        public String path() {
            return request.uri();
        }

        @Override
        public String header(String name) {
            return request.headers().get(name);
        }

        @Override
        public String cookie(String name) {
            String cookieHeader = request.headers().get(HttpHeaderNames.COOKIE);
            if (cookieHeader == null) {
                return null;
            }

            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieHeader);
            if (cookies.isEmpty()) {
                return null;
            }

            for (Cookie cookie : cookies) {
                if (Objects.equals(name, cookie.name())) {
                    return cookie.value();
                }
            }

            return null;
        }

        @Override
        public byte[] content() {
            ByteBuf content = request.content();
            byte[] bytes = new byte[content.readableBytes()];
            content.readBytes(bytes);
            return bytes;
        }
    }

    private static class NettyHttpServerResponse implements HttpResponse {

        private HttpResponseStatus status = HttpResponseStatus.OK;
        private HttpHeaders headers = new DefaultHttpHeaders();
        private InputStream content;

        @Override
        public void status(Status status) {
            this.status = HttpResponseStatus.valueOf(status.code());
        }

        @Override
        public void content(InputStream content) {
            this.content = content;
        }

        @Override
        public void header(String name, String value) {
            headers.set(name, value);
        }

        @Override
        public void cookie(String name, String value) {
            Cookie cookie = new DefaultCookie(name, value);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            if (value.isEmpty()) {
                cookie.setMaxAge(0);
            }
            headers.add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
        }

        private HttpResponseStatus status() {
            return status;
        }

        private InputStream content() {
            return content;
        }

        private HttpHeaders headers() {
            return headers;
        }
    }
}
