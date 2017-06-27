package org.java.notification.push.application;

import org.java.notification.push.OS;

/**
 * Created by msamoylych on 05.05.2017.
 */
public class FCMApplication extends Application {

    private String serverKey;

    public OS os() {
        return OS.ANDROID;
    }

    public String serverKey() {
        return serverKey;
    }

    public void serverKey(String serverKey) {
        this.serverKey = serverKey;
    }
}
