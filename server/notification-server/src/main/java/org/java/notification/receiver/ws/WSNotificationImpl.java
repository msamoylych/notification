package org.java.notification.receiver.ws;

import org.java.notification.receiver.ReceiverService;
import org.java.notification.receiver.model.RequestModel;
import org.java.notification.receiver.model.ResponseModel;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPBinding;

/**
 * Created by msamoylych on 24.05.2017.
 */
@WebService(serviceName = "NotificationService",
        portName = "NotificationServicePort",
        endpointInterface = "org.java.notification.receiver.ws.WSNotification")
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class WSNotificationImpl implements WSNotification {

    @Resource
    private WebServiceContext context;

    private ReceiverService receiverService;

    @Override
    public ResponseModel send(RequestModel request) {
        return receiverService.receive(httpServletRequest(), request);
    }

    private HttpServletRequest httpServletRequest() {
        MessageContext messageContext = context.getMessageContext();
        return (HttpServletRequest) messageContext.get(MessageContext.SERVLET_REQUEST);
    }

    void setReceiverService(ReceiverService receiverService) {
        this.receiverService = receiverService;
    }
}
