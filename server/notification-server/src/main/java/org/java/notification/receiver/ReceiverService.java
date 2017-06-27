package org.java.notification.receiver;

import org.java.notification.push.ApplicationStorage;
import org.java.notification.push.Push;
import org.java.notification.push.PushStorage;
import org.java.notification.push.application.Application;
import org.java.notification.receiver.model.PushModel;
import org.java.notification.receiver.model.ResultModel;
import org.java.notification.router.Router;
import org.java.notification.storage.StorageException;
import org.java.notification.user.System;
import org.java.notification.user.SystemStorage;
import org.java.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by msamoylych on 22.06.2017.
 */
@Component
public class ReceiverService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiverService.class);

    private static final Random RND = new Random();

    private static final ResultModel UNDEFINED_SYSTEM_CODE = new ResultModel("undefined-system-code");
    private static final ResultModel INVALID_SYSTEM_CODE = new ResultModel("invalid-system-code");
    private static final ResultModel SYSTEM_LOCKED = new ResultModel("system-locked");
    private static final ResultModel DUPLICATE_PUSH = new ResultModel("duplicate-push");
    private static final ResultModel ERROR = new ResultModel("duplicate-push");

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
    public ResultModel receive(String systemCode, List<PushModel> pushModels) {
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
            LOGGER.error("System with code <{}> is locked", systemCode);
            return SYSTEM_LOCKED;
        }

        LOGGER.info("Received {} push(es) from {}", pushModels.size(), system.name());

        List<Push<?>> pushes = new ArrayList<>(pushModels.size());
        List<ResultModel.Result> results = new ArrayList<>(pushModels.size());
        for (PushModel pushModel : pushModels) {
            ResultModel.Result result = new ResultModel.Result();
            results.add(result);
            result.id = pushModel.id;

            if (!checkPushModel(pushModel)) {
                result.error = "invalid-push";
                continue;
            }

            Application application = applicationStorage.application(systemCode, pushModel.os, pushModel.packageName);
            if (application == null) {
                LOGGER.error("PushModel {}: application not found", pushModel.id);
                result.error = "application-not-found";
                continue;
            }

            Push push = new Push();
            pushes.add(push);
            push.application(application);

            push.token(pushModel.token);

            push.title(pushModel.title);
            push.body(pushModel.body);
            push.icon(pushModel.icon);

            push.systemId(system.id());
            push.extId(pushModel.id);
        }

        try {
            pushStorage.save(pushes);
        } catch (StorageException ex) {
            if (ex.isConstraintViolation()) {
                LOGGER.error(ex.getMessage(), ex);
                return DUPLICATE_PUSH;
            } else {
                int err = RND.nextInt(1000000);
                LOGGER.error("Error #{} occurred", err, ex);
                return new ResultModel("error-#" + err);
            }
        }

        Iterator<Push<?>> itr = pushes.iterator();
        for (ResultModel.Result result : results) {
            if (result.error == null) {
                result.msgId = itr.next().id();
            }
        }

        router.route(pushes);

        ResultModel resultModel = new ResultModel();
        resultModel.results = results;
        return resultModel;
    }

    private boolean checkPushModel(PushModel pushModel) {
        if (StringUtils.isEmpty(pushModel.id)) {
            LOGGER.error("PushModel id not specified");
            return false;
        }
        if (!StringUtils.checkLength(pushModel.id, 36)) {
            LOGGER.error("PushModel {}: id length more than 36 characters", pushModel.id);
            return false;
        }
        if (!StringUtils.checkLength(pushModel.token, 256)) {
            LOGGER.error("PushModel {}: token length more than 256 characters", pushModel.id);
            return false;
        }
        if (!StringUtils.checkLength(pushModel.title, 256)) {
            LOGGER.error("PushModel {}: title length more than 256 characters", pushModel.id);
            return false;
        }
        if (!StringUtils.checkLength(pushModel.body, 4000)) {
            LOGGER.error("PushModel {}: body length more than 4000 characters", pushModel.id);
            return false;
        }
        if (!StringUtils.checkLength(pushModel.icon, 16)) {
            LOGGER.error("PushModel {}: icon length more than 16 characters", pushModel.id);
            return false;
        }
        return true;
    }
}
