package org.java.utils.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by msamoylych on 20.04.2017.
 */
public enum Status {
    OK(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    private static final Map<Integer, Status> MAP = new HashMap<>();

    static {
        Arrays.stream(values()).forEach(status -> MAP.put(status.code, status));
    }

    Status(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static Status status(int code) {
        return MAP.get(code);
    }
}
