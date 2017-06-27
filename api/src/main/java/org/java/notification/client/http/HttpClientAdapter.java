package org.java.notification.client.http;

import org.java.notification.Message;
import org.java.notification.client.ClientAdapter;
import org.java.utils.http.Method;
import org.java.utils.http.Status;

/**
 * Created by msamoylych on 05.04.2017.
 */
public interface HttpClientAdapter<M extends Message> extends ClientAdapter {

    default boolean http2() {
        return false;
    }

    String path();

    Method method();

    void headers(Headers headers, M msg);

    String content(M msg);

    void handleResponse(M msg, Status status, Headers headers, String response);

    interface Headers {
        void set(String name, String value);
        String get(String name);
    }
}
