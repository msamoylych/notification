package org.java.netty.server;

import org.java.netty.NettyFactory;
import org.java.netty.server.http.NettyHttpServer;
import org.java.notification.server.ServerFactory;
import org.java.notification.server.ServerHandler;
import org.java.notification.server.http.HttpServerHandler;
import org.springframework.stereotype.Component;

/**
 * Created by msamoylych on 20.04.2017.
 */
@Component
public class NettyServerFactory extends NettyFactory implements ServerFactory<NettyServer> {

    @Override
    public NettyServer createServer(String name, int port, ServerHandler handler) {
        if (handler instanceof HttpServerHandler) {
            return new NettyHttpServer(netty, name, port, (HttpServerHandler) handler);
        }

        throw new IllegalArgumentException("Unsupported handler type: " + handler.getClass().getName());
    }
}
