package org.java.notification.client;

import org.java.notification.Message;
import org.springframework.stereotype.Service;

/**
 * Created by msamoylych on 06.04.2017.
 */
@Service
public interface ClientFactory {

    <M extends Message> Client<M> createClient(ClientAdapter<M> adapter);
}