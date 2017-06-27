package org.java.notification.push.application;

import org.java.notification.push.OS;

/**
 * Created by msamoylych on 30.05.2017.
 */
public class MPNSApplication extends Application {

    @Override
    public OS os() {
        return OS.WINDOWS_PHONE;
    }
}
