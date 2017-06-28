package org.java.notification.sender;

import org.java.notification.Message;
import org.java.notification.client.ClientFactory;
import org.java.notification.client.SendException;
import org.springframework.stereotype.Service;

/**
 * Created by msamoylych on 17.04.2017.
 */
@Service
public interface Sender<M extends Message> {

    void init(ClientFactory clientFactory);

    void send(M msg) throws SendException;
}
