package org.java.notification.sender.push.fcm;

import org.java.notification.client.http.HttpClientAdapter;
import org.java.notification.push.Push;
import org.java.notification.push.PushStorage;
import org.java.notification.push.State;
import org.java.notification.push.application.FCMApplication;
import org.java.notification.sender.AbstractSender;
import org.java.notification.storage.StorageException;
import org.java.utils.Json;
import org.java.utils.http.ContentType;
import org.java.utils.http.Header;
import org.java.utils.http.Method;
import org.java.utils.http.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by msamoylych on 27.06.2017.
 */
@Component
public class FCMSender extends AbstractSender<Push<FCMApplication>> implements HttpClientAdapter<Push<FCMApplication>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FCMSender.class);

    private static final String HOST = "fcm.googleapis.com";
    private static final int PORT = 443;
    private static final String PATH = "/fcm/send";

    private static final String AUTHORIZATION = "authorization";
    private static final String KEY = "key=";

    private static final Pattern RESPONSE_PATTERN = Pattern.compile("(\"message_id\":\"(?<messageId>.+?)\"|\"error\":\"(?<error>\\w+?)\")");
    private static final String MESSAGE_ID = "messageId";
    private static final String ERROR = "error";

    private final PushStorage pushStorage;

    public FCMSender(PushStorage pushStorage) {
        this.pushStorage = pushStorage;
    }

    @Override
    public String host() {
        return HOST;
    }

    @Override
    public int port() {
        return PORT;
    }

    @Override
    public boolean http2() {
        return true;
    }

    @Override
    public String path() {
        return PATH;
    }

    @Override
    public Method method() {
        return Method.POST;
    }

    @Override
    public void headers(Headers headers, Push<FCMApplication> msg) {
        headers.set(Header.CONTENT_TYPE, ContentType.APPLICATION_JSON);
        headers.set(AUTHORIZATION, KEY + msg.application().serverKey());
    }

    @Override
    public String content(Push<FCMApplication> push) {
        return Json
                .start()
                .add("to", push.token())
                .startObject("notification")
                .add("title", push.title())
                .add("body", push.body())
                .add("icon", push.icon())
                .endObject()
                .end();
    }

    @Override
    public void handleResponse(Push<FCMApplication> push, Status status, Headers headers, String content) {
        boolean success = false;
        switch (status) {
            case OK:
                Matcher matcher = RESPONSE_PATTERN.matcher(content);
                if (matcher.find()) {
                    String messageId = matcher.group(MESSAGE_ID);
                    if (messageId != null) {
                        push.pnsId(messageId);
                        success = true;
                    } else {
                        String error = matcher.group(ERROR);
                        LOGGER.error("Push [id:{}] - pns error: {}", push.id(), error);
                        push.pnsError(error);
                    }
                }
                break;
            case BAD_REQUEST:
                LOGGER.error("Push [id:{}] - invalid request: {}", push.id(), content(push));
                break;
            case UNAUTHORIZED:
                LOGGER.error("Push [id:{}], application [id:{};package:{}] - invalid authentication key",
                        push.application().id(), push.application().packageName());
                break;
            default:
                LOGGER.error("Push [id:{}] - server code {}", push.id(), status);
        }

        if (success) {
            push.state(State.SENT);
        } else {
            push.state(State.FAILED);
        }

        try {
            pushStorage.update(push);
        } catch (StorageException ex) {
            LOGGER.error("Push [id:{}] - update failed", ex);
        }
    }
}
