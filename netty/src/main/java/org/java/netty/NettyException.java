package org.java.netty;

/**
 * Created by msamoylych on 04.04.2017.
 */
public class NettyException extends RuntimeException {

    public NettyException(String message) {
        super(message);
    }

    public NettyException(String message, Throwable cause) {
        super(message, cause);
    }
}
