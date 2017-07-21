package org.java.netty.client;

import org.java.netty.NettyFactory;
import org.java.netty.client.http.NettyHttpClient;
import org.java.netty.client.http2.NettyHttp2Client;
import org.java.notification.Message;
import org.java.notification.client.Client;
import org.java.notification.client.ClientAdapter;
import org.java.notification.client.ClientFactory;
import org.java.notification.client.http.Http2ClientAdapter;
import org.java.notification.client.http.HttpClientAdapter;
import org.springframework.stereotype.Component;

/**
 * Created by msamoylych on 06.04.2017.
 */
@Component
@SuppressWarnings("unused")
public class NettyClientFactory extends NettyFactory implements ClientFactory {

    @Override
    public <M extends Message> Client<M> createClient(ClientAdapter<M> adapter) {
        if (adapter instanceof Http2ClientAdapter) {
            return new NettyHttp2Client<>(netty, (Http2ClientAdapter<M>) adapter);
        } else if (adapter instanceof HttpClientAdapter) {
            return new NettyHttpClient<>(netty, (HttpClientAdapter<M>) adapter);
        }

        throw new IllegalArgumentException("Unsupported adapter: " + adapter.getClass().getName());
    }
}