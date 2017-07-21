package org.java.notification.receiver.rs;

import org.java.notification.receiver.model.RequestModel;
import org.java.notification.receiver.model.ResponseModel;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by msamoylych on 12.07.2017.
 */
@Path("/")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
public interface RSNotification {

    @POST
    @Path("send")
    ResponseModel send(@Context HttpServletRequest httpServletRequest, RequestModel request);
}
