package org.java.notification.server.http;

import org.java.notification.server.Server;
import org.java.utils.lifecycle.SmartLifecycle;

/**
 * Created by msamoylych on 20.04.2017.
 */
public final class HttpServer extends SmartLifecycle {

    private final Server server;

    public HttpServer(Server server) {
        this.server = server;
    }

    @Override
    protected void doStart() {
        server.start();
    }

    @Override
    protected void doStop() {
        server.stop();
    }

    @Override
    public int getPhase() {
        return 1;
    }
}
