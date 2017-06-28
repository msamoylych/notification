package org.java.notification.sender.push.fcm;

import org.java.notification.client.http.HttpClientAdapter;
import org.java.notification.push.Push;
import org.java.notification.push.application.FCMApplication;
import org.java.notification.sender.AbstractSender;
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

    private static final String NOT_REGISTERED = "NotRegistered";
    private static final String INVALID_REGISTRATION = "InvalidRegistration";
    private static final String MISMATCH_SENDER_ID = "MismatchSenderId";

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
    public String content(Push<FCMApplication> msg) {
        return Json
                .start()
                .add("to", msg.token())
                .startObject("notification")
                .add("title", msg.title())
                .add("body", msg.body())
                .add("icon", msg.icon())
                .endObject()
                .end();
    }

    @Override
    public void handleResponse(Push<FCMApplication> msg, Status status, Headers headers, String content) {
        LOGGER.info(content);
        switch (status) {
            case OK:
                Matcher matcher = RESPONSE_PATTERN.matcher(content);
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
                LOGGER.info(content);
        }
    }
}
