package org.java.notification.receiver.ws;

import org.java.notification.receiver.Receiver;
import org.java.notification.receiver.ReceiverService;
import org.java.utils.lifecycle.SmartLifecycle;
import org.java.utils.settings.SettingsRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.ws.Endpoint;

/**
 * Created by msamoylych on 24.05.2017.
 */
@Component
@SuppressWarnings("unused")
public class WSReceiver extends SmartLifecycle implements Receiver, SettingsRegister {
    private static final Logger LOGGER = LoggerFactory.getLogger(WSReceiver.class);

    private final boolean enabled = setting("WS_RECEIVER_ENABLED", "Запуск", false);
    private final int port = setting("WS_RECEIVER_PORT", "Порт", 8787);
    private final String path = setting("WS_RECEIVER_PATH", "Путь", "");

    private final Endpoint endpoint;

    public WSReceiver(ReceiverService receiverService) {
        WSNotificationImpl implementor = new WSNotificationImpl();
        implementor.setReceiverService(receiverService);
        endpoint = Endpoint.create(implementor);
    }

    @Override
    protected boolean isEnabled() {
        return enabled;
    }

    @Override
    protected void doStart() throws Exception {
        String address = "http://0.0.0.0:" + port + "/" + path;
        endpoint.publish(address);
        LOGGER.info("Listening on {}", address);
    }

    @Override
    protected void doStop() throws Exception {
        endpoint.stop();
    }

    @Override
    public int getPhase() {
        return 2;
    }
}
