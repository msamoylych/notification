package org.java.netty.client;

import org.java.netty.client.http.NettyHttpClient;
import org.java.netty.client.http2.NettyHttp2Client;
import org.java.notification.Message;
import org.java.notification.client.Client;
import org.java.notification.client.ClientAdapter;
import org.java.notification.client.ClientFactory;
import org.java.notification.client.http.HttpClientAdapter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by msamoylych on 06.04.2017.
 */
@Component
public class NettyClientFactory implements ClientFactory, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    @SuppressWarnings("unchecked")
    public <M extends Message> Client<M> createClient(ClientAdapter<M> adapter) {
        if (adapter instanceof HttpClientAdapter) {
            HttpClientAdapter<M> httpClientAdapter = (HttpClientAdapter<M>) adapter;
            if (httpClientAdapter.http2()) {
                return applicationContext.getBean(NettyHttp2Client.class, httpClientAdapter);
            } else {
                return applicationContext.getBean(NettyHttpClient.class, httpClientAdapter);
            }
        } else {
            throw new IllegalArgumentException("Unsupported adapter: " + adapter);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
