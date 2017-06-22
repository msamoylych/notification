package org.java.notification.client.http;

import org.java.notification.Message;
import org.java.notification.client.ClientAdapter;
import org.java.utils.http.Method;
import org.java.utils.http.Status;

/**
 * Created by msamoylych on 05.04.2017.
 */
public interface HttpClientAdapter<M extends Message> extends ClientAdapter<M> {

    boolean http2();

    String host();

    int port();

    Method method();

    String path();

    void headers(Headers headers, M msg);

    String content(M msg);

    void handleResponse(Status status, Headers headers, String response, M msg);

    interface Headers {
        String HOST = "Host";

        String CONTENT_LENGTH = "Content-Length";
        String CONTENT_TYPE = "Content-Type";
        String JSON = "application/json";

        void set(String name, String value);
    }
}
