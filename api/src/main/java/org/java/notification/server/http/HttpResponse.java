package org.java.notification.server.http;

import org.java.notification.server.Response;
import org.java.utils.StringUtils;
import org.java.utils.http.ContentType;
import org.java.utils.http.Header;
import org.java.utils.http.Status;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by msamoylych on 20.04.2017.
 */
public interface HttpResponse extends Response {

    void status(Status status);

    void header(String name, String value);

    void cookie(String name, String value);

    default void removeCookie(String name) {
        cookie(name, "");
    }

    void content(InputStream stream);

    default void json(String json) {
        header(Header.CONTENT_TYPE, ContentType.APPLICATION_JSON);
        content(new ByteArrayInputStream(StringUtils.getBytes(json)));
    }

    default void html(String html) {
        header(Header.CONTENT_TYPE, ContentType.TEXT_HTML);
        content(new ByteArrayInputStream(StringUtils.getBytes(html)));
    }
}
