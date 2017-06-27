package org.java.netty.client;

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
public class NettyClientFactory implements ClientFactory {

    @Override
    public <M extends Message> Client<M> createClient(ClientAdapter adapter) {
        if (adapter instanceof HttpClientAdapter) {
            HttpClientAdapter<M> httpClientAdapter = (HttpClientAdapter<M>) adapter;
            if (httpClientAdapter.http2()) {
                return new NettyHttp2Client<>(httpClientAdapter);
            } else {
                return new NettyHttpClient<>(httpClientAdapter);
            }
        } else {
            throw new IllegalArgumentException("Unsupported adapter: " + adapter.getClass().getSimpleName());
        }
    }
}
