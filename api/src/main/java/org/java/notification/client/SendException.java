package org.java.notification.client;

/**
 * Created by msamoylych on 17.04.2017.
 */
public class SendException extends Exception {

    public SendException() {
    }

    public SendException(String message) {
        super(message);
    }

    public SendException(String message, Throwable cause) {
        super(message, cause);
    }

    public SendException(Throwable cause) {
        super(cause);
    }
}
