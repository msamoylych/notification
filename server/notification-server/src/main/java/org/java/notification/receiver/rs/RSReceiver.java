package org.java.notification.receiver.rs;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.java.notification.receiver.Receiver;
import org.java.notification.receiver.ReceiverService;
import org.java.notification.setting.ReceiverSetting;
import org.java.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.Marshaller;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by msamoylych on 12.07.2017.
 */
@Component("RS")
@SuppressWarnings("unused")
public class RSReceiver implements Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(RSReceiver.class);

    private final JAXRSServerFactoryBean serverFactoryBean;

    public RSReceiver(ReceiverService receiverService) {
        RSNotificationImpl resource = new RSNotificationImpl();
        resource.setReceiverService(receiverService);

        serverFactoryBean = new JAXRSServerFactoryBean();
        serverFactoryBean.setResourceClasses(RSNotification.class);
        serverFactoryBean.setResourceProvider(RSNotification.class, new SingletonResourceProvider(resource));
        HashMap<String, Object> marshallerProperties = new HashMap<>();
        marshallerProperties.put(Marshaller.JAXB_FRAGMENT, true);
        JAXBElementProvider jaxbProvider = new JAXBElementProvider();
        jaxbProvider.setMarshallerProperties(marshallerProperties);
        serverFactoryBean.setProviders(Arrays.asList(new JacksonJaxbJsonProvider(), jaxbProvider));
    }

    @Override
    public void start(ReceiverSetting setting) {
        String address = "http://" + setting.host() + ":" + setting.port() + "/" + StringUtils.notNull(setting.path());
        serverFactoryBean.setAddress(address);
        serverFactoryBean.create();
        LOGGER.info("Receiver started on {}", address);
    }

    @Override
    public void stop() {
        serverFactoryBean.getServer().stop();
    }
}
