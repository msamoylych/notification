package org.java.notification.user;

import org.java.notification.storage.PreloadStorage;
import org.java.notification.storage.StorageException;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by msamoylych on 08.06.2017.
 */
@Repository
public class SystemStorage extends PreloadStorage {

    private static final String CODE = "SYSTEM";
    private static final String SELECT = "SELECT id, code, name, locked FROM SYSTEM";

    private final Map<Long, System> systemsById = new HashMap<>();
    private final Map<String, System> systemsByCode = new HashMap<>();

    @Override
    public String code() {
        return CODE;
    }

    public System system(Long id) {
        try {
            read.lock();
            return systemsById.get(id);
        } finally {
            read.unlock();
        }
    }

    public System system(String code) {
        try {
            read.lock();
            return systemsByCode.get(code);
        } finally {
            read.unlock();
        }
    }

    @Override
    protected void doLoad() throws StorageException {
        withPreparedStatement(SELECT, rs -> {
            try {
                write.lock();

                systemsById.clear();
                systemsByCode.clear();

                while (rs.next()) {
                    System system = new System();
                    system.id(rs.getLong());
                    system.code(rs.getString());
                    system.name(rs.getString());
                    system.locked(rs.getYNBoolean());
                    systemsById.put(system.id(), system);
                    systemsByCode.put(system.code(), system);
                }
            } finally {
                write.unlock();
            }
            return null;
        });
    }
}