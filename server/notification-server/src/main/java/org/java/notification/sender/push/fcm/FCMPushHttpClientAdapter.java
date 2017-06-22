package org.java.notification.sender.push.fcm;

import org.java.notification.client.http.HttpClientAdapter;
import org.java.notification.push.Push;
import org.java.notification.push.application.ApplicationAndroid;
import org.java.utils.Json;
import org.java.utils.http.Method;
import org.java.utils.http.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by msamoylych on 05.04.2017.
 */
@Component
public class FCMPushHttpClientAdapter implements HttpClientAdapter<Push<ApplicationAndroid>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FCMPushHttpClientAdapter.class);

    private static final String NAME = "FCM";
    private static final String HOST = "fcm-http.googleapis.com";
    private static final int PORT = 443;
    private static final String PATH = "/fcm/send";

    private static final String AUTHORIZATION = "Authorization";
    private static final String KEY = "key=";

    private static final Pattern RESPONSE_PATTERN = Pattern.compile("(\"message_id\":\"(?<messageId>.+?)\"|\"error\":\"(?<error>\\w+?)\")");
    private static final String MESSAGE_ID = "messageId";
    private static final String ERROR = "error";

    private static final String NOT_REGISTERED = "NotRegistered";
    private static final String INVALID_REGISTRATION = "InvalidRegistration";
    private static final String MISMATCH_SENDER_ID = "MismatchSenderId";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public boolean http2() {
        return true;
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
    public Method method() {
        return Method.POST;
    }

    @Override
    public String path() {
        return PATH;
    }

    @Override
    public void headers(Headers headers, Push<ApplicationAndroid> msg) {
        headers.set(Headers.CONTENT_TYPE, Headers.JSON);
        headers.set(AUTHORIZATION, KEY + msg.application().serverKey());
    }

    @Override
    public String content(Push<ApplicationAndroid> msg) {
        return Json.start()
                .add("to", msg.device().token())
                .startObject("notification")
                .add("title", msg.title())
                .add("body", msg.body())
                .add("icon", msg.icon())
                .endObject()
                .end();
    }

    @Override
    public void handleResponse(Status status, Headers headers, String response, Push<ApplicationAndroid> msg) {
        switch (status) {
            case OK:
                Matcher matcher = RESPONSE_PATTERN.matcher(response);
                if (matcher.find()) {
                    String messageId = matcher.group(MESSAGE_ID);
                    if (messageId != null) {
                        LOGGER.info(messageId);
                    } else {
                        String error = matcher.group(ERROR);
                        switch (error) {
                            case MISMATCH_SENDER_ID:
                                LOGGER.error("Invalid key");
                                break;
                            case NOT_REGISTERED:
                            case INVALID_REGISTRATION:
                                LOGGER.error("Invalid token");
                                break;
                            default:
                                LOGGER.error("Unknown error: {}", error);
                        }
                    }
                }
                break;
            case BAD_REQUEST:
                LOGGER.error("Invalid request: {}", content(msg));
                break;
            case UNAUTHORIZED:
                LOGGER.error("Invalid authentication key");
                break;
            default:
        }
    }
}