package org.java.notification.admin;

import org.java.notification.server.http.HttpRequest;
import org.java.notification.server.http.HttpResponse;
import org.java.notification.server.http.HttpServerHandler;
import org.java.utils.http.ContentType;
import org.java.utils.http.Header;
import org.java.utils.http.Method;
import org.java.utils.http.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by msamoylych on 20.04.2017.
 */
public class AdminHttpServerHandler implements HttpServerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminHttpServerHandler.class);

    private static final String SLASH = "/";

    private static final String PREFIX = "www";
    private static final String MAIN = SLASH + "main.html";

    private static final String LOGIN = SLASH + "login";
    private static final String LOGOUT = SLASH + "logout";

    private static final String CACHE = "public, max-age=86400";

    private AuthenticationService authenticationService;

    public AdminHttpServerHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        try {
            Method method = request.method();
            switch (method) {
                case GET:
                    get(request, response);
                    break;
                case POST:
                    post(request, response);
                    break;
                default:
                    response.status(Status.METHOD_NOT_ALLOWED);
            }
        } catch (Throwable th) {
            LOGGER.error("Server handle error", th);
            response.status(Status.INTERNAL_SERVER_ERROR);
        }
    }

    private void get(HttpRequest request, HttpResponse response) {
        String path = request.path();
        if (path.equals(SLASH)) {
            path = MAIN;
        }

        resource(path, response);
    }

    private void post(HttpRequest request, HttpResponse response) throws Throwable {
        switch (request.path()) {
            case LOGIN:
                authenticationService.login(request, response);
                break;
            case LOGOUT:
                authenticationService.logout(request, response);
                break;
            default:
                response.status(Status.NOT_FOUND);
        }
    }

    private void resource(String path, HttpResponse response) {
        InputStream is;
        if (Boolean.getBoolean("useFiles")) {
            try {
                is = new FileInputStream("admin-server/src/main/resources/www" + path);
            } catch (FileNotFoundException ex) {
                LOGGER.warn("Resource {} not found", path);
                response.status(Status.NOT_FOUND);
                return;
            }
        } else {
            is = ClassLoader.getSystemResourceAsStream(PREFIX + path);
        }

        if (is == null) {
            LOGGER.warn("Resource {} not found", path);
            response.status(Status.NOT_FOUND);
            return;
        }

        response.content(is);

        response.header(Header.CONTENT_TYPE, ContentType.contentType(path));
        response.header(Header.CACHE_CONTROL, CACHE);
    }
}
