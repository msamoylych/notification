package org.java.utils.storage;

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
    protected final Lock write = lock.writeLock();
    protected final Lock read = lock.readLock();

    public abstract String code();

    void load() {
        try {
            doLoad();
        } catch (StorageException ex) {
            LOGGER.error("Can't load {}", code(), ex);
        }
    }

    protected abstract void doLoad() throws StorageException;
}
