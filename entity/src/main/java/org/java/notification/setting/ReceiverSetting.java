package org.java.notification.setting;

import org.java.notification.Entity;

/**
 * Created by msamoylych on 25.05.2017.
 */
public class ReceiverSetting extends Entity {

    private String server;

    private Type type;

    private String host;

    private Integer port;

    private String path;

    public String server() {
        return server;
    }

    public void server(String server) {
        this.server = server;
    }

    public Type type() {
        return type;
    }

    public void type(Type type) {
        this.type = type;
    }

    public String host() {
        return host;
    }

    public void host(String host) {
        this.host = host;
    }

    public Integer port() {
        return port;
    }

    public void port(Integer port) {
        this.port = port;
    }

    public String path() {
        return path;
    }

    public void path(String path) {
        this.path = path;
    }

    public enum Type {
        WS,
        RS
    }
}
