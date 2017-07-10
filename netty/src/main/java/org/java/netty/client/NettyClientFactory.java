package org.java.netty.client;

import org.java.netty.Netty;
import org.java.netty.client.http.NettyHttpClient;
import org.java.netty.client.http2.NettyHttp2Client;
import org.java.notification.Message;
import org.java.notification.client.Client;
import org.java.notification.client.ClientAdapter;
import org.java.notification.client.ClientFactory;
import org.java.notification.client.http.HttpClientAdapter;
import org.springframework.stereotype.Component;

/**
 * Created by msamoylych on 06.04.2017.
 */
@Component
@SuppressWarnings("unused")
public class NettyClientFactory implements ClientFactory {

    private final Netty netty;

    public NettyClientFactory(Netty netty) {
        this.netty = netty;
    }

    @Override
    public <M extends Message> Client<M> createClient(ClientAdapter<M> adapter) {
        if (adapter instanceof HttpClientAdapter) {
            HttpClientAdapter<M> httpClientAdapter = (HttpClientAdapter<M>) adapter;
            if (httpClientAdapter.http2()) {
                return new NettyHttp2Client<>(netty, httpClientAdapter);
            } else {
                return new NettyHttpClient<>(netty, httpClientAdapter);
            }
        } else {
            throw new IllegalArgumentException("Unsupported adapter: " + adapter);
        }
    }
}
