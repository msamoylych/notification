package org.java.notification.setting;

import org.java.notification.storage.Storage;
import org.java.notification.storage.StorageException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.java.notification.setting.ReceiverSetting.Type;

/**
 * Created by msamoylych on 25.05.2017.
 */
@Repository
public class ReceiverStorage extends Storage {

    private static final String SELECT = "SELECT id, type, host, port, path FROM RECEIVER";

    public List<ReceiverSetting> getReceivers() throws StorageException {
        return withPreparedStatement(SELECT, rs -> {
            List<ReceiverSetting> result = new ArrayList<>();
            while (rs.next()) {
                ReceiverSetting setting = new ReceiverSetting();
                setting.id(rs.getLong());
                setting.type(rs.getEnum(Type.class));
                setting.host(rs.getString());
                setting.port(rs.getInt());
                setting.path(rs.getString());
                result.add(setting);
            }
            return result;
        });
    }
}
