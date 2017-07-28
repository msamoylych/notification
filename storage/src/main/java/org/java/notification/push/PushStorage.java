package org.java.notification.push;

import org.java.utils.storage.Storage;
import org.java.utils.storage.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.List;

/**
 * Created by msamoylych on 30.05.2017.
 */
@Repository
public class PushStorage extends Storage {
    private static final Logger LOGGER = LoggerFactory.getLogger(PushStorage.class);

    private static final String INSERT_PUSH = "{call P_INSERT_PUSH(?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String INSERT_PUSHES = "{call P_INSERT_PUSHES(?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final String UPDATE = "UPDATE PUSH SET state = ?, pns_id = ?, pns_error = ? WHERE id = ?";

    public static final String T_NUMBER_20 = "T_NUMBER_20";
    public static final String T_VARCHAR2_16 = "T_VARCHAR2_16";
    public static final String T_VARCHAR2_36 = "T_VARCHAR2_36";
    public static final String T_VARCHAR2_256 = "T_VARCHAR2_256";
    public static final String T_VARCHAR2_4000 = "T_VARCHAR2_4000";

    public void save(Push<?> push) throws StorageException {
        withCallableStatement(INSERT_PUSH,
                st -> {
                    st.registerOutParameter(Types.NUMERIC);
                    st.set(push.application());
                    st.set(push.token());
                    st.set(push.title());
                    st.set(push.body());
                    st.set(push.icon());
                    st.set(push.system());
                    st.set(push.extId());
                },
                st -> push.id(st.getLong()));
    }

    public void save(List<Push<?>> pushes) throws StorageException {
        if (pushes.isEmpty()) {
            return;
        }
        if (pushes.size() == 1) {
            save(pushes.get(0));
            return;
        }

        withCallableStatement(INSERT_PUSHES,
                st -> {
                    int s = pushes.size();
                    Long[] applicationIds = new Long[s];
                    String[] tokens = new String[s];
                    String[] titles = new String[s];
                    String[] bodies = new String[s];
                    String[] icons = new String[s];
                    Long[] systemIds = new Long[s];
                    String[] extIds = new String[s];

                    int i = 0;
                    for (Push push : pushes) {
                        applicationIds[i] = push.application().id();
                        tokens[i] = push.token();
                        titles[i] = push.title();
                        bodies[i] = push.body();
                        icons[i] = push.icon();
                        systemIds[i] = push.system().id();
                        extIds[i] = push.extId();
                        i++;
                    }

                    st.registerOutParameter(Types.ARRAY, T_NUMBER_20);
                    st.set(T_NUMBER_20, applicationIds);
                    st.set(T_VARCHAR2_256, tokens);
                    st.set(T_VARCHAR2_256, titles);
                    st.set(T_VARCHAR2_4000, bodies);
                    st.set(T_VARCHAR2_16, icons);
                    st.set(T_NUMBER_20, systemIds);
                    st.set(T_VARCHAR2_36, extIds);
                },
                st -> {
                    BigDecimal[] ids = (BigDecimal[]) st.getArray();

                    int i = 0;
                    for (Push push : pushes) {
                        push.id(ids[i].longValue());
                        i++;
                    }
                });
    }

    public void update(Push<?> push) {
        try {
            withPreparedStatement(UPDATE,
                    (PreparedStatementWrapper<?> st) -> {
                        st.set(push.state());
                        st.set(push.pnsId());
                        st.set(push.pnsError());
                        st.set(push.id());
                    });
        } catch (StorageException ex) {
            LOGGER.error("{} - update failed", push, ex);
        }
    }
}