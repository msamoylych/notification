package org.java.notification.client;

import org.java.notification.Message;

/**
 * Created by msamoylych on 12.04.2017.
 */
public interface ClientAdapter<M extends Message> {

    String host();

    int port();

    void fail(M msg, Throwable th);
}
