package org.java.notification.user;

import org.java.utils.storage.Entity;

/**
 * Created by msamoylych on 24.05.2017.
 */
public class System extends Entity {

    private String code;

    private String name;

    private boolean locked;

    public String code() {
        return code;
    }

    public void code(String code) {
        this.code = code;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public boolean locked() {
        return locked;
    }

    public void locked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public String toString() {
        return "System [id:" + id() + ",code:" + code + ']';
    }
}
