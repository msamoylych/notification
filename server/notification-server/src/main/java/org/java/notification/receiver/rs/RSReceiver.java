package org.java.notification.receiver.rs;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.java.notification.receiver.Receiver;
import org.java.notification.receiver.ReceiverService;
import org.java.notification.setting.ReceiverSetting;
import org.java.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by msamoylych on 12.07.2017.
 */
@Component
public class RSReceiver implements Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(RSReceiver.class);

    private final JAXRSServerFactoryBean sf;

    public RSReceiver(ReceiverService receiverService) {
        RSNotification resource = new RSNotification();
        resource.setReceiverService(receiverService);

        sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(RSNotification.class);
        sf.setResourceProvider(RSNotification.class, new SingletonResourceProvider(resource));
        sf.setProvider(new JacksonJaxbJsonProvider());
    }

    @Override
    public ReceiverSetting.Type type() {
        return ReceiverSetting.Type.RS;
    }

    @Override
    public void start(ReceiverSetting setting) {
        String address = "http://" + setting.host() + ":" + setting.port() + "/" + StringUtils.notNull(setting.path());
        sf.setAddress(address);
        sf.create();
        LOGGER.info("Receiver started on {}", address);
    }

    @Override
    public void stop() {
        sf.getServer().stop();
    }
}
