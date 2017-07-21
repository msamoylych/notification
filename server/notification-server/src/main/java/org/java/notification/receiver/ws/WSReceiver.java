package org.java.notification.receiver.ws;

import org.java.notification.receiver.Receiver;
import org.java.notification.receiver.ReceiverService;
import org.java.notification.setting.ReceiverSetting;
import org.java.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.ws.Endpoint;

/**
 * Created by msamoylych on 24.05.2017.
 */
@Component("WS")
@SuppressWarnings("unused")
public class WSReceiver implements Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(WSReceiver.class);

    private final Endpoint endpoint;

    public WSReceiver(ReceiverService receiverService) {
        WSNotificationImpl implementor = new WSNotificationImpl();
        implementor.setReceiverService(receiverService);
        endpoint = Endpoint.create(implementor);
    }

    @Override
    public void start(ReceiverSetting setting) {
        String address = "http://0.0.0.0:" + setting.port() + "/" + StringUtils.notNull(setting.path());
        endpoint.publish(address);
        LOGGER.info("Started on {}", address);
    }

    @Override
    public void stop() {
        endpoint.stop();
    }
}
