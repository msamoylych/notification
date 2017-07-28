package org.java.notification.admin;

import org.java.notification.server.http.HttpRequest;
import org.java.notification.server.http.HttpResponse;
import org.java.notification.user.WebUser;
import org.java.notification.user.WebUserStorage;
import org.java.utils.HashUtils;
import org.java.utils.Json;
import org.java.utils.StringUtils;
import org.java.utils.storage.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by msamoylych on 02.05.2017.
 */
public class AuthenticationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    private static final String SESSION_ID = "SESSIONID";

    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";

    private static final String RESULT = "result";
    private static final String OK = "ok";
    private static final String BAD = "bad";

    private static final String MENU = "menu";

    private WebUserStorage storage;
    private Sessions sessions;

    public AuthenticationService(WebUserStorage storage, Sessions sessions) {
        this.storage = storage;
        this.sessions = sessions;
    }

    void login(HttpRequest request, HttpResponse response) throws StorageException {
        String sessionId = null;
        WebUser user = null;

        byte[] content = request.content();
        if (content.length > 0) {
            Map<String, String> params = new HashMap<>();//Json.parse(content);
            String login = params.get(LOGIN);
            String password = params.get(PASSWORD);

            if (login != null && password != null) {
                user = storage.findUser(login);
                if (checkUser(user) && checkPassword(user, password)) {
                    LOGGER.info("User logged in ({})", user.login());
                    sessionId = sessions.create(user);
                    response.cookie(SESSION_ID, sessionId);
                }
            }
        } else {
            sessionId = request.cookie(SESSION_ID);
            user = sessions.user(sessionId);
        }

        if (sessionId != null && user != null) {
            response.json(Json.start()
                    .add(RESULT, OK)
                    .add(LOGIN, user.login())
                    .end());
        } else {
            response.json(Json.json(RESULT, BAD));
        }
    }

    void logout(HttpRequest request, HttpResponse response) {
        String sessionId = request.cookie(SESSION_ID);
        if (sessionId != null) {
            sessions.remove(sessionId);
            response.removeCookie(SESSION_ID);
        }
        response.json(Json.json(RESULT, OK));
    }

    private static boolean checkUser(WebUser user) {
        return user != null;
    }

    private static boolean checkPassword(WebUser user, String password) {
        return Arrays.equals(
                HashUtils.sha512(StringUtils.getBytes(password), HashUtils.hexToBytes(user.salt())),
                HashUtils.hexToBytes(user.password())
        );
    }
}
