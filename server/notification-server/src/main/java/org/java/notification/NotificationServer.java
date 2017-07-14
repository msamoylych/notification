package org.java.notification;

import org.java.notification.client.ClientFactory;
import org.java.notification.receiver.Receiver;
import org.java.notification.sender.Sender;
import org.java.notification.setting.ReceiverSetting;
import org.java.notification.setting.ReceiverStorage;
import org.java.notification.storage.StorageException;
import org.java.utils.BeanUtils;
import org.java.utils.lifecycle.SmartLifecycle;
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
@SuppressWarnings("unused")
public class NotificationServer extends SmartLifecycle implements ApplicationContextAware {

    private final List<Sender> senders = new ArrayList<>();
    private final List<Receiver> receivers = new ArrayList<>();

    private ClientFactory clientFactory;
    private ReceiverStorage receiverStorage;

    private ApplicationContext applicationContext;

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
        BeanUtils.forEachBeanOfType(applicationContext, Sender.class, sender -> {
            sender.start(clientFactory);
            senders.add(sender);
        });
    }

    private void startReceivers() throws StorageException {
        List<ReceiverSetting> receiverSettings = receiverStorage.getReceivers();
        receiverSettings.forEach(receiverSetting -> {
            Receiver receiver = applicationContext.getBean(receiverSetting.type(), Receiver.class);
            receiver.start(receiverSetting);
            receivers.add(receiver);
        });
    }

    @Override
    protected void doStop() {
        stopReceivers();
        stopSenders();
    }

    private void stopSenders() {
        for (Sender sender : senders) {

        }
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
        this.applicationContext = applicationContext;
    }
}
