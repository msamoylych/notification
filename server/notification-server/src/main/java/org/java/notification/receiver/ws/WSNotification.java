package org.java.notification.receiver.ws;

import org.java.notification.receiver.model.RequestModel;
import org.java.notification.receiver.model.ResponseModel;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * Created by msamoylych on 24.05.2017.
 */
@WebService
public interface WSNotification {

    @WebResult(name = "response")
    ResponseModel send(
            @WebParam(name = "request") RequestModel request);
}
