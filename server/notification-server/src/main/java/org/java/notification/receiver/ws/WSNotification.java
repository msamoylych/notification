package org.java.notification.receiver.ws;

import org.java.notification.receiver.model.PushModel;
import org.java.notification.receiver.model.ResultModel;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import java.util.List;

/**
 * Created by msamoylych on 24.05.2017.
 */
@WebService
public interface WSNotification {

    @WebResult(name = "result")
    ResultModel send(
            @WebParam(name = "code") String code,
            @WebParam(name = "push") List<PushModel> pushModels);
}
