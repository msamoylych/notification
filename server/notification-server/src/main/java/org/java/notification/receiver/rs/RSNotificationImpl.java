package org.java.notification.receiver.rs;

import org.java.notification.receiver.ReceiverService;
import org.java.notification.receiver.model.RequestModel;
import org.java.notification.receiver.model.ResponseModel;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by msamoylych on 12.07.2017.
 */
public class RSNotificationImpl implements RSNotification {

    private ReceiverService receiverService;

    @Override
    public ResponseModel send(HttpServletRequest httpServletRequest, RequestModel request) {
        return receiverService.receive(httpServletRequest, request);
    }

    void setReceiverService(ReceiverService receiverService) {
        this.receiverService = receiverService;
    }
}
