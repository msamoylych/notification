package org.java.notification.receiver.ws;

import org.java.notification.receiver.ReceiverService;
import org.java.notification.receiver.model.PushModel;
import org.java.notification.receiver.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPBinding;
import java.util.List;

/**
 * Created by msamoylych on 24.05.2017.
 */
@Component
@WebService(serviceName = "NotificationService",
        portName = "NotificationServicePort",
        endpointInterface = "org.java.notification.receiver.ws.WSNotification")
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class WSNotificationImpl implements WSNotification {
    private static final Logger LOGGER = LoggerFactory.getLogger(WSNotification.class);

    @Resource
    private WebServiceContext context;

    @Autowired
    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    private ReceiverService receiverService;

    @Override
    public ResultModel send(String code, List<PushModel> pushModels) {
        logIP();
        return receiverService.receive(code, pushModels);
    }

    private void logIP() {
        MessageContext messageContext = context.getMessageContext();
        HttpServletRequest request = (HttpServletRequest) messageContext.get(MessageContext.SERVLET_REQUEST);
        String ip = request.getLocalAddr();
        LOGGER.info("Request from {}", ip);
    }
}
