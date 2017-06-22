package org.java.netty.server;

import org.java.netty.server.http.NettyHttpServer;
import org.java.notification.server.ServerFactory;
import org.java.notification.server.ServerHandler;
import org.java.notification.server.http.HttpServerHandler;

/**
 * Created by msamoylych on 20.04.2017.
 */
public class NettyServerFactory implements ServerFactory<NettyServer> {

    @Override
    public NettyServer createServer(String name, int port, ServerHandler handler) {
        if (handler instanceof HttpServerHandler) {
            return new NettyHttpServer(name, port, (HttpServerHandler) handler);
        }
        throw new IllegalArgumentException("Unknown adapter type: " + handler.getClass());
    }
}
