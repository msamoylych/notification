package org.java.notification.receiver.ws;

import org.java.notification.push.ApplicationStorage;
import org.java.notification.push.Push;
import org.java.notification.push.PushStorage;
import org.java.notification.push.application.Application;
import org.java.notification.receiver.ws.model.PushModel;
import org.java.notification.receiver.ws.model.ResultModel;
import org.java.notification.user.System;
import org.java.notification.user.SystemStorage;
import org.java.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;
import java.util.ArrayList;
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

    private final SystemStorage systemStorage;
    private final ApplicationStorage applicationStorage;
    private final PushStorage pushStorage;

    public WSNotificationImpl(SystemStorage systemStorage, ApplicationStorage applicationStorage, PushStorage pushStorage) {
        this.systemStorage = systemStorage;
        this.applicationStorage = applicationStorage;
        this.pushStorage = pushStorage;
    }

    @Override
    public ResultModel send(String code, List<PushModel> pushModels) {
        System system = checkSystem(code);
        if (system == null) {
            return new ResultModel("system-not-found");
        }

        LOGGER.info("{} message(s) received from {}", pushModels.size(), system.name());

        List<Push> pushes = new ArrayList<>(pushModels.size());
        List<ResultModel> results = new ArrayList<>(pushModels.size());
        for (PushModel pushModel : pushModels) {
            Application application = applicationStorage.application(code, pushModel.os(), pushModel.packageName());
        }
        return null;
    }

    private System checkSystem(String code) {
        if (StringUtils.isEmpty(code)) {
            LOGGER.warn("Code is undefined");
            return null;
        }

        System system = systemStorage.system(code);
        if (system == null) {
            LOGGER.warn("System with code {} not found", code);
            return null;
        } else if (system.locked()) {
            LOGGER.warn("System with code {} is locked", code);
            return null;
        }

        return system;
    }
}
