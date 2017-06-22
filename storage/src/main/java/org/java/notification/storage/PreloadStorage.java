package org.java.notification.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by msamoylych on 09.06.2017.
 */
public abstract class PreloadStorage extends Storage {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreloadStorage.class);

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock write = lock.writeLock();
    private final Lock read = lock.readLock();

    public abstract String code();

    void load() {
        try {
            write.lock();
            doLoad();
        } catch (StorageException ex) {
            LOGGER.error("Can't load " + code(), ex);
        } finally {
            write.unlock();
        }
    }

    protected abstract void doLoad() throws StorageException;

    protected <R> R get(Function<R> func) {
        try {
            read.lock();
            return func.apply();
        } finally {
            read.unlock();
        }
    }

    @FunctionalInterface
    protected interface Function<R> {
        R apply();
    }
}
