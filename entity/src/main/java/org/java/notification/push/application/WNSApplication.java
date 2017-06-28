package org.java.notification.push.application;

import org.java.notification.push.PNS;

/**
 * Created by msamoylych on 30.05.2017.
 */
public class WNSApplication extends Application {

    @Override
    public PNS pns() {
        return PNS.WNS;
    }
}
