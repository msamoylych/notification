package org.java.notification.receiver.rs;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.java.notification.receiver.Receiver;
import org.java.notification.receiver.ReceiverService;
import org.java.utils.lifecycle.SmartLifecycle;
import org.java.utils.settings.SettingsRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.Marshaller;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by msamoylych on 12.07.2017.
 */
@Component
@SuppressWarnings("unused")
public class RSReceiver extends SmartLifecycle implements Receiver, SettingsRegister {
    private static final Logger LOGGER = LoggerFactory.getLogger(RSReceiver.class);

    private final boolean enabled = setting("RS_RECEIVER_ENABLED", "Запуск", false);
    private final int port = setting("RS_RECEIVER_PORT", "Порт", 8787);
    private final String path = setting("RS_RECEIVER_PATH", "Путь", "");

    private final JAXRSServerFactoryBean serverFactoryBean;

    public RSReceiver(ReceiverService receiverService) {
        serverFactoryBean = new JAXRSServerFactoryBean();

        serverFactoryBean.setResourceClasses(RSNotification.class);
        RSNotificationImpl resource = new RSNotificationImpl();
        resource.setReceiverService(receiverService);
        serverFactoryBean.setResourceProvider(RSNotification.class, new SingletonResourceProvider(resource));

        JacksonJaxbJsonProvider jsonProvider = new JacksonJaxbJsonProvider();
        JAXBElementProvider jaxbProvider = new JAXBElementProvider();
        HashMap<String, Object> marshallerProperties = new HashMap<>();
        marshallerProperties.put(Marshaller.JAXB_FRAGMENT, true);
        jaxbProvider.setMarshallerProperties(marshallerProperties);
        serverFactoryBean.setProviders(Arrays.asList(jsonProvider, jaxbProvider));
    }

    @Override
    protected boolean isEnabled() {
        return enabled;
    }

    @Override
    protected void doStart() throws Exception {
        String address = "http://0.0.0.0:" + port + "/" + path;
        serverFactoryBean.setAddress(address);
        serverFactoryBean.create();
        LOGGER.info("Listening on {}", address);
    }

    @Override
    protected void doStop() throws Exception {
        serverFactoryBean.getServer().stop();
    }

    @Override
    public int getPhase() {
        return 2;
    }
}
