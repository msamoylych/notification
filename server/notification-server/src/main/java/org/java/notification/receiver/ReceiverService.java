package org.java.notification.receiver;

import org.java.notification.push.ApplicationStorage;
import org.java.notification.push.Push;
import org.java.notification.push.PushStorage;
import org.java.notification.push.application.Application;
import org.java.notification.receiver.model.PushModel;
import org.java.notification.receiver.model.RequestModel;
import org.java.notification.receiver.model.ResponseModel;
import org.java.notification.router.Router;
import org.java.notification.storage.StorageException;
import org.java.notification.user.System;
import org.java.notification.user.SystemStorage;
import org.java.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by msamoylych on 22.06.2017.
 */
@Component
public class ReceiverService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiverService.class);

    private static final Random RND = new Random();

    private static final ResponseModel UNDEFINED_SYSTEM_CODE = new ResponseModel("undefined-system-code");
    private static final ResponseModel INVALID_SYSTEM_CODE = new ResponseModel("invalid-system-code");
    private static final ResponseModel SYSTEM_LOCKED = new ResponseModel("system-locked");
    private static final ResponseModel EMPTY_REQUEST = new ResponseModel("empty-request");
    private static final ResponseModel DUPLICATE_PUSH = new ResponseModel("duplicate-push");

    private final SystemStorage systemStorage;
    private final ApplicationStorage applicationStorage;
    private final PushStorage pushStorage;
    private final Router router;

    public ReceiverService(SystemStorage systemStorage, ApplicationStorage applicationStorage, PushStorage pushStorage,
                           Router router) {
        this.systemStorage = systemStorage;
        this.applicationStorage = applicationStorage;
        this.pushStorage = pushStorage;
        this.router = router;
    }

    @SuppressWarnings("unchecked")
    public ResponseModel receive(HttpServletRequest httpServletRequest, RequestModel request) {
        String remoteHost = httpServletRequest.getRemoteHost();
        String remoteAddr = httpServletRequest.getRemoteAddr();
        if (Objects.equals(remoteHost, remoteAddr)) {
            LOGGER.info("Request from {}", remoteHost);
        } else {
            LOGGER.info("Request from {} ({})", remoteHost, remoteAddr);
        }

        String systemCode = request.system;
        if (StringUtils.isEmpty(systemCode)) {
            LOGGER.error("System code not specified");
            return UNDEFINED_SYSTEM_CODE;
        }

        System system = systemStorage.system(systemCode);
        if (system == null) {
            LOGGER.error("System with code <{}> not found", systemCode);
            return INVALID_SYSTEM_CODE;
        }
        if (system.locked()) {
            LOGGER.error("{} is locked", system);
            return SYSTEM_LOCKED;
        }

        List<PushModel> pushModels = request.pushes;

        if (pushModels == null || pushModels.isEmpty()) {
            LOGGER.error("Empty request");
            return EMPTY_REQUEST;
        }

        LOGGER.info("Received {} push(es) from {}", pushModels.size(), system.name());

        try {
            List<Push<?>> pushes = new ArrayList<>(pushModels.size());
            List<ResponseModel.Result> results = new ArrayList<>(pushModels.size());
            for (PushModel pushModel : pushModels) {
                ResponseModel.Result result = new ResponseModel.Result(pushModel.id);
                results.add(result);

                if (!checkPushModel(pushModel)) {
                    result.error = "invalid-push";
                    continue;
                }

                Application application = applicationStorage.application(system.id(), pushModel.pns, pushModel.packageName);
                if (application == null) {
                    LOGGER.error("{}: application not found", pushModel);
                    result.error = "application-not-found";
                    continue;
                }

                Push push = new Push();
                pushes.add(push);
                push.system(system);
                push.extId(pushModel.id);
                push.application(application);

                push.token(pushModel.token);

                push.title(pushModel.title);
                push.body(pushModel.body);
                push.icon(pushModel.icon);
            }

            pushStorage.save(pushes);

            Iterator<Push<?>> itr = pushes.iterator();
            for (ResponseModel.Result result : results) {
                if (result.error == null) {
                    result.msgId = itr.next().id();
                }
            }

            router.route(pushes);

            return new ResponseModel(results);
        } catch (Throwable th) {
            if (th instanceof StorageException && ((StorageException) th).isConstraintViolation()) {
                LOGGER.error(th.getMessage(), th);
                return DUPLICATE_PUSH;
            } else {
                int err = RND.nextInt(1000000);
                LOGGER.error("Error #{} occurred", err, th);
                return new ResponseModel("error-#" + err);
            }
        }
    }

    private boolean checkPushModel(PushModel pushModel) {
        if (StringUtils.isEmpty(pushModel.id)) {
            LOGGER.error("PushModel id not specified");
            return false;
        }
        if (!StringUtils.checkLength(pushModel.id, 36)) {
            LOGGER.error("{}: id length more than 36 characters", pushModel);
            return false;
        }
        if (!StringUtils.checkLength(pushModel.token, 256)) {
            LOGGER.error("{}: token length more than 256 characters", pushModel);
            return false;
        }
        if (!StringUtils.checkLength(pushModel.title, 256)) {
            LOGGER.error("{}: title length more than 256 characters", pushModel);
            return false;
        }
        if (!StringUtils.checkLength(pushModel.body, 4000)) {
            LOGGER.error("{}: body length more than 4000 characters", pushModel);
            return false;
        }
        if (!StringUtils.checkLength(pushModel.icon, 16)) {
            LOGGER.error("{}: icon length more than 16 characters", pushModel);
            return false;
        }
        return true;
    }
}
