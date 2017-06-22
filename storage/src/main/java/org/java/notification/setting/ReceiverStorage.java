package org.java.notification.setting;

import org.java.notification.storage.Storage;
import org.java.notification.storage.StorageException;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.java.notification.setting.ReceiverSetting.Type;

/**
 * Created by msamoylych on 25.05.2017.
 */
@Repository
public class ReceiverStorage extends Storage {

    private static final String SELECT = "SELECT id, server, type, host, port, path FROM RECEIVER";
    private static final String SELECT_BY_SERVER = SELECT + " WHERE server IS NULL OR server = ?";

    public List<ReceiverSetting> getReceivers() throws StorageException {
        return withPreparedStatement(SELECT, this::parse);
    }

    public List<ReceiverSetting> getServerReceivers(String server) throws StorageException {
        return withPreparedStatement(SELECT_BY_SERVER, st -> st.setString(server), this::parse);
    }

    private List<ReceiverSetting> parse(ResultSetWrapper rs) throws SQLException {
        List<ReceiverSetting> result = new ArrayList<>();
        while (rs.next()) {
            ReceiverSetting rset = new ReceiverSetting();
            rset.id(rs.getLong());
            rset.server(rs.getString());
            rset.type(Type.valueOf(rs.getString()));
            rset.host(rs.getString());
            rset.port(rs.getInt());
            rset.path(rs.getString());
            result.add(rset);
        }
        return result;
    }
}
