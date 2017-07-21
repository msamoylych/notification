package org.java.notification.setting;

import org.java.notification.Entity;

/**
 * Created by msamoylych on 25.05.2017.
 */
public class ReceiverSetting extends Entity {

    private String type;

    private Integer port;

    private String path;

    public String type() {
        return type;
    }

    public void type(String type) {
        this.type = type;
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
}
