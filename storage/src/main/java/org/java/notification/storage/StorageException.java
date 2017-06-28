package org.java.notification.storage;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Created by msamoylych on 04.04.2017.
 */
public class StorageException extends Exception {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String sql, Throwable cause) {
        super("SQL: " + sql, cause);
    }

    public boolean isConstraintViolation() {
        Throwable cause = getCause();
        return cause != null && cause instanceof SQLIntegrityConstraintViolationException;
    }
}
