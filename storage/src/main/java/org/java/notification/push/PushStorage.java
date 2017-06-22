package org.java.notification.push;

import org.java.notification.storage.Storage;
import org.java.notification.storage.StorageException;
import org.java.notification.storage.Type;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.List;

/**
 * Created by msamoylych on 30.05.2017.
 */
@Repository
public class PushStorage extends Storage {

    private static final String INSERT_PUSH = "{? = call F_INSERT_PUSH(?, ?, ?, ?)}";
    private static final String INSERT_PUSHES = "{? = call F_INSERT_PUSHES(?, ?, ?, ?)}";

    public void insert(Push push) throws StorageException {
        withCallableStatement(INSERT_PUSH, st -> {
            st.registerOutParameter(Types.NUMERIC);
            st.setLong(push.application().id());
            st.setString(push.token());
            st.setString(push.title());
            st.setString(push.body());
        }, st -> push.id(st.getLong()));
    }

    public void insert(List<Push> pushes) throws StorageException {
        withCallableStatement(INSERT_PUSHES, st -> {
            int s = pushes.size();
            Long[] applicationIds = new Long[s];
            String[] tokens = new String[s];
            String[] titles = new String[s];
            String[] bodies = new String[s];

            int i = 0;
            for (Push push : pushes) {
                applicationIds[i] = push.application().id();
                tokens[i] = push.token();
                titles[i] = push.title();
                bodies[i] = push.body();
                i++;
            }

            st.registerOutParameter(Types.ARRAY, Type.T_NUMBER_20);
            st.setArray(Type.T_NUMBER_20, applicationIds);
            st.setArray(Type.T_VARCHAR2_256, tokens);
            st.setArray(Type.T_VARCHAR2_256, titles);
            st.setArray(Type.T_VARCHAR2_4000, bodies);
        }, st -> {
            BigDecimal[] ids = (BigDecimal[]) st.getArray();

            int i = 0;
            for (Push push : pushes) {
                push.id(ids[i].longValue());
                i++;
            }
        });
    }
}