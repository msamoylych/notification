package org.java.notification.sender;

import org.java.notification.Message;
import org.java.notification.client.Client;
import org.java.notification.client.ClientAdapter;
import org.java.notification.client.ClientFactory;
import org.java.notification.client.SendException;

/**
 * Created by msamoylych on 27.06.2017.
 */
public abstract class BaseSender<M extends Message> implements Sender<M>, ClientAdapter<M> {

    private final Client<M> client;

    public BaseSender(ClientFactory clientFactory) {
        this.client = clientFactory.createClient(this);
    }

    @Override
    public void send(M msg) throws SendException {
        client.send(msg);
    }
}
