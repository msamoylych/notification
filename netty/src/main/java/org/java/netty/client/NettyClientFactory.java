package org.java.netty.client;

import org.java.netty.client.http.NettyHttpClient;
import org.java.netty.client.http2.NettyHttp2Client;
import org.java.notification.Message;
import org.java.notification.client.ClientFactory;
import org.java.notification.client.http.HttpClientAdapter;

/**
 * Created by msamoylych on 06.04.2017.
 */
public class NettyClientFactory<M extends Message> implements ClientFactory<M, NettyClient<M>, HttpClientAdapter<M>> {

    @Override
    public NettyClient<M> createClient(HttpClientAdapter<M> adapter) {
        if (adapter.http2()) {
            return new NettyHttp2Client<>(adapter);
        } else {
            return new NettyHttpClient<>(adapter);
        }
    }
}
