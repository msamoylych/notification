package org.java.notification.server;

import org.springframework.stereotype.Component;

/**
 * Created by msamoylych on 20.04.2017.
 */
@Component
public interface ServerFactory<S extends Server> {

    S createServer(String name, int port, ServerHandler adapter);
}
