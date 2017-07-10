package org.java.notification;

import org.java.notification.client.ClientFactory;
import org.java.notification.receiver.Receiver;
import org.java.notification.sender.Sender;
import org.java.notification.setting.ReceiverSetting;
import org.java.notification.setting.ReceiverStorage;
import org.java.notification.storage.StorageException;
import org.java.utils.BeanUtils;
import org.java.utils.lifecycle.SmartLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by msamoylych on 25.05.2017.
 */
@Service
public class NotificationServer extends SmartLifecycle implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServer.class);

    private final List<Sender> senders = new ArrayList<>();
    private final List<Receiver> receivers = new ArrayList<>();

    private ClientFactory clientFactory;
    private ReceiverStorage receiverStorage;

    public NotificationServer(ClientFactory clientFactory, ReceiverStorage receiverStorage) {
        this.clientFactory = clientFactory;
        this.receiverStorage = receiverStorage;
    }

    @Override
    protected void doStart() throws Exception {
        startSenders();
        startReceivers();
    }

    private void startSenders() {
        for (Sender sender : senders) {
            sender.start(clientFactory);
        }
    }

    private void startReceivers() throws StorageException {
        List<ReceiverSetting> receiverSettings = receiverStorage.getReceivers();

        for (Receiver receiver : receivers) {
            ReceiverSetting setting = null;

            for (ReceiverSetting receiverSetting : receiverSettings) {
                if (receiverSetting.type() == receiver.type()) {
                    setting = receiverSetting;
                    break;
                }
            }

            if (setting != null) {
                receiver.start(setting);
            }
        }
    }

    @Override
    protected void doStop() {
        stopReceivers();
    }

    private void stopReceivers() {
        for (Receiver receiver : receivers) {
            receiver.stop();
        }
    }

    @Override
    public int getPhase() {
        return 1;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        senders.addAll(BeanUtils.beansOfType(applicationContext, Sender.class));
        receivers.addAll(BeanUtils.beansOfType(applicationContext, Receiver.class));
    }
}
