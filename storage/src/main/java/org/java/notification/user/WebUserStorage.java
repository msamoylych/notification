package org.java.notification.user;

import org.java.notification.storage.Storage;
import org.java.notification.storage.StorageException;
import org.springframework.stereotype.Repository;

/**
 * Created by msamoylych on 02.05.2017.
 */
@Repository
public class WebUserStorage extends Storage {

    private static final String SELECT = "SELECT id, login, password, salt FROM WEB_USER";
    private static final String SELECT_BY_LOGIN = SELECT + " WHERE login = ?";

    public WebUser findUser(String login) throws StorageException {
        return withPreparedStatement(SELECT_BY_LOGIN,
                st -> st.setString(login),
                rs -> {
                    WebUser webUser = new WebUser();
                    webUser.id(rs.getLong());
                    webUser.login(rs.getString());
                    webUser.password(rs.getString());
                    webUser.salt(rs.getString());
                    return webUser;
                });
    }
}