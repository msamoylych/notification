package org.java.notification.receiver;

import org.java.notification.setting.ReceiverSetting;
import org.springframework.stereotype.Service;

/**
 * Created by msamoylych on 24.05.2017.
 */
@Service
public interface Receiver {

    void start(ReceiverSetting setting);

    void stop();
}
