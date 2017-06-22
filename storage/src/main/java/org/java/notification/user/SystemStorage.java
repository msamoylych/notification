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
    private static final String SELECT = "SELECT code, name, locked FROM SYSTEM";

    private final Map<String, System> systems = new HashMap<>();

    public System system(String code) {
        return get(() -> systems.get(code));
    }

    @Override
    public String code() {
        return CODE;
    }

    @Override
    protected void doLoad() throws StorageException {
        withPreparedStatement(SELECT, rs -> {
            systems.clear();

            while (rs.next()) {
                System system = new System();
                system.code(rs.getString());
                system.name(rs.getString());
                system.locked(rs.getYNBoolean());
                systems.put(system.code(), system);
            }

            return null;
        });
    }
}