package org.java.notification.client;

import org.java.notification.Message;
import org.springframework.stereotype.Component;

/**
 * Created by msamoylych on 06.04.2017.
 */
@Component
public interface ClientFactory<M extends Message, C extends Client<M>, A extends ClientAdapter<M>> {

    C createClient(A adapter);
}