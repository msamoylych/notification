package org.java.netty.server.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.java.netty.server.NettyServer;
import org.java.notification.server.http.HttpServerHandler;

/**
 * Created by msamoylych on 17.04.2017.
 */
public class NettyHttpServer extends NettyServer {

    public NettyHttpServer(String name, int port, HttpServerHandler handler) {
        super(name, port);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast(NettyHttpServerHandler.handlers(handler));
            }
        });
    }
}
