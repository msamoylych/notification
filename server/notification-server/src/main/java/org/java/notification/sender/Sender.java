package org.java.notification.sender;

import org.java.notification.Message;
import org.java.notification.client.SendException;

/**
 * Created by msamoylych on 17.04.2017.
 */
public interface Sender<M extends Message> {

    void send(M msg) throws SendException;
}
