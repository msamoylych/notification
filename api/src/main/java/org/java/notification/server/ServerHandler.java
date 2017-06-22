package org.java.notification.server;

/**
 * Created by msamoylych on 17.04.2017.
 */
public interface ServerHandler<REQ extends Request, RES extends Response> {

    void handle(REQ request, RES response);
}
