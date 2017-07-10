package org.java.netty.server;

import org.java.netty.Netty;
import org.java.netty.server.http.NettyHttpServer;
import org.java.notification.server.ServerFactory;
import org.java.notification.server.ServerHandler;
import org.java.notification.server.http.HttpServerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by msamoylych on 20.04.2017.
 */
@Component
public class NettyServerFactory implements ServerFactory<NettyServer> {

    @Autowired
    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    private Netty netty;

    @Override
    public NettyServer createServer(String name, int port, ServerHandler handler) {
        if (handler instanceof HttpServerHandler) {
            return new NettyHttpServer(netty, name, port, (HttpServerHandler) handler);
        }
        throw new IllegalArgumentException("Unknown adapter type: " + handler.getClass());
    }
}
