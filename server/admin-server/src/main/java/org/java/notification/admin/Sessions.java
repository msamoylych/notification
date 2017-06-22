package org.java.notification.admin;

import org.java.notification.user.WebUser;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by msamoylych on 25.04.2017.
 */
class Sessions {

    private final Map<String, WebUser> users = new ConcurrentHashMap<>();

    String create(WebUser user) {
        String sessionId = UUID.randomUUID().toString();
        users.put(sessionId, user);
        return sessionId;
    }

    WebUser user(String sessionId) {
        return sessionId != null ? users.get(sessionId) : null;
    }

    void remove(String sessionId) {
        users.remove(sessionId);
    }
}