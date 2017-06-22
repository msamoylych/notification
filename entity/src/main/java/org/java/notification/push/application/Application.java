package org.java.notification.push.application;

import org.java.notification.Entity;
import org.java.notification.push.OS;

/**
 * Created by msamoylych on 14.04.2017.
 */
public abstract class Application extends Entity {

    private String packageName;

    public abstract OS os();

    public String packageName() {
        return packageName;
    }

    public void packageName(String packageName) {
        this.packageName = packageName;
    }
}