package org.java.notification.client;

import org.java.notification.Message;

/**
 * Created by msamoylych on 05.04.2017.
 */
public interface Client<M extends Message> {

    void send(M msg);
}
