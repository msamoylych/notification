package org.java.notification.push.application;

import org.java.notification.push.PNS;
import org.java.utils.storage.Entity;

/**
 * Created by msamoylych on 14.04.2017.
 */
public abstract class Application extends Entity {

    private Long systemId;

    private String packageName;

    public abstract PNS pns();

    public Long systemId() {
        return systemId;
    }

    public void systemId(Long systemId) {
        this.systemId = systemId;
    }

    public String packageName() {
        return packageName;
    }

    public void packageName(String packageName) {
        this.packageName = packageName;
    }
}