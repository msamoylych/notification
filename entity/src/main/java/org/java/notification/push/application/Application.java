package org.java.notification.push.application;

import org.java.notification.Entity;
import org.java.notification.push.PNS;

/**
 * Created by msamoylych on 14.04.2017.
 */
public abstract class Application extends Entity {

    private String systemId;

    private String packageName;

    public abstract PNS pns();

    public String systemId() {
        return systemId;
    }

    public void systemId(String systemId) {
        this.systemId = systemId;
    }

    public String packageName() {
        return packageName;
    }

    public void packageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [id:" + id() + ']';
    }
}