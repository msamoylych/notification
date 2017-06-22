package org.java.notification.push;

import org.java.notification.Entity;

/**
 * Created by msamoylych on 14.04.2017.
 */
public class Device extends Entity {

    private Long applicationId;

    private String token;

    public Long applicationId() {
        return applicationId;
    }

    public void applicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String token() {
        return token;
    }

    public void token(String token) {
        this.token = token;
    }
}
