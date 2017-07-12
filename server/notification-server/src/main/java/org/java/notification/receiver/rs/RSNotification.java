package org.java.notification.receiver.rs;

import org.java.notification.receiver.ReceiverService;
import org.java.notification.receiver.model.RequestModel;
import org.java.notification.receiver.model.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by msamoylych on 12.07.2017.
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RSNotification {
    private static final Logger LOGGER = LoggerFactory.getLogger(RSNotification.class);

    private ReceiverService receiverService;

    @GET
    public ResponseModel index() {
        return new ResponseModel();
    }

    @POST
    public ResponseModel send(@Context HttpServletRequest httpServletRequest, RequestModel request) {
        logIP(httpServletRequest);
        return receiverService.receive(request);
    }

    private void logIP(HttpServletRequest httpServletRequest) {
        String ip = httpServletRequest.getRemoteAddr();
        LOGGER.info("Request {}", ip);
    }

    void setReceiverService(ReceiverService receiverService) {
        this.receiverService = receiverService;
    }
}
