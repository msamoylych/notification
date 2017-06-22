package org.java.notification.server.http;

import org.java.notification.server.Request;
import org.java.utils.http.Method;

/**
 * Created by msamoylych on 20.04.2017.
 */
public interface HttpRequest extends Request {

    Method method();

    String path();

    String header(String name);

    String cookie(String name);

    byte[] content();
}
