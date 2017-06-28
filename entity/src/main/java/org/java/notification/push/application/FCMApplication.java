package org.java.notification.push.application;

import org.java.notification.push.PNS;

/**
 * Created by msamoylych on 05.05.2017.
 */
public class FCMApplication extends Application {

    private String serverKey;

    public FCMApplication(String serverKey) {
        this.serverKey = serverKey;
    }

    public PNS pns() {
        return PNS.FCM;
    }

    public String serverKey() {
        return serverKey;
    }

    public void serverKey(String serverKey) {
        this.serverKey = serverKey;
    }
}
