package org.java.netty.client.http2;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2Headers;
import org.java.notification.Message;
import org.java.notification.client.http.Http2ClientAdapter;
import org.java.utils.http.Status;

import static io.netty.handler.codec.http.HttpScheme.HTTPS;

/**
 * Created by msamoylych on 06.04.2017.
 */
class NettyHttp2ClientAdapter<M extends Message> {

    private final Http2ClientAdapter<M> adapter;

    NettyHttp2ClientAdapter(Http2ClientAdapter<M> adapter) {
        this.adapter = adapter;
    }

    Http2Headers headers(M msg) {
        Http2Headers headers = new DefaultHttp2Headers()
                .method(method())
                .scheme(HTTPS.name())
                .authority(adapter.host())
                .path(adapter.path());
        adapter.headers(NettyHttp2ClientHeaders.wrap(headers), msg);
        return headers;
    }

    String content(M msg) {
        return adapter.content(msg);
    }

    void handleResponse(M msg, Http2Headers headers, String response) {
        HttpResponseStatus status = HttpResponseStatus.parseLine(headers.status());
        adapter.handleResponse(msg, Status.status(status.code()), new NettyHttp2ClientHeaders(headers), response);
    }

    void fail(M msg, Throwable th) {
        adapter.fail(msg, th);
    }

    private CharSequence method() {
        switch (adapter.method()) {
            case GET:
                return HttpMethod.GET.asciiName();
            case POST:
                return HttpMethod.POST.asciiName();
            default:
                throw new IllegalStateException("Unsupported method: " + adapter.method());
        }
    }

    private static class NettyHttp2ClientHeaders implements Http2ClientAdapter.Headers {
        private Http2Headers headers;

        private NettyHttp2ClientHeaders(Http2Headers headers) {
            this.headers = headers;
        }

        private static NettyHttp2ClientHeaders wrap(Http2Headers headers) {
            return new NettyHttp2ClientHeaders(headers);
        }

        @Override
        public void set(String name, String value) {
            headers.set(name, value);
        }

        @Override
        public String get(String name) {
            return headers.get(name).toString();
        }
    }
}
