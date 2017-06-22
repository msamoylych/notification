package org.java.notification;

import org.java.notification.receiver.Receiver;
import org.java.notification.setting.ReceiverSetting;
import org.java.notification.setting.ReceiverStorage;
import org.java.notification.storage.PreloadStorageController;
import org.java.notification.storage.StorageException;
import org.java.utils.BeanUtils;
import org.java.utils.lifecycle.SmartLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by msamoylych on 25.05.2017.
 */
@Service
public class NotificationServer extends SmartLifecycle {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServer.class);

    private final List<Receiver> receivers = new ArrayList<>();

    private PreloadStorageController preloadStorageController;
    private ReceiverStorage receiverStorage;

    public NotificationServer(PreloadStorageController preloadStorageController, ReceiverStorage receiverStorage) {
        this.preloadStorageController = preloadStorageController;
        this.receiverStorage = receiverStorage;
    }

    @Override
    protected void doStart() throws Exception {
        InetAddress local = InetAddress.getLocalHost();
        String hostName = local.getHostName();
        LOGGER.info("Server name: {}", hostName);

        preloadStorageController.init();

        startReceivers(hostName);
    }

    private void startReceivers(String server) throws StorageException {
        List<ReceiverSetting> receiverSettings = receiverStorage.getServerReceivers(server);

        for (Receiver receiver : BeanUtils.beansOfType(Receiver.class)) {
            ReceiverSetting setting = null;

            for (ReceiverSetting receiverSetting : receiverSettings) {
                if (receiverSetting.type() == receiver.type()) {
                    setting = receiverSetting;
                    if (receiverSetting.server() != null) {
                        break;
                    }
                }
            }

            if (setting != null) {
                receiver.start(setting);
                receivers.add(receiver);
            }
        }
    }

    @Override
    protected void doStop() {
        stopReceivers();
    }

    @Override
    public int getPhase() {
        return 1;
    }

    private void stopReceivers() {
        for (Receiver receiver : receivers) {
            receiver.stop();
        }
    }
}
