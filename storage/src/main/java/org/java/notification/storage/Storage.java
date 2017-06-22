package org.java.notification.storage;

import oracle.jdbc.OracleConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Created by msamoylych on 06.06.2017.
 */
public abstract class Storage {
    private static final Logger LOGGER = LoggerFactory.getLogger(Storage.class);

    @Autowired
    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    private DataSource dataSource;

    protected void withStatement(String sql) throws StorageException {
        withStatement(sql, null);
    }

    protected <R> R withStatement(String sql, Function<R> parse) throws StorageException {
        assert dataSource != null;

        try (Connection connection = dataSource.getConnection()) {
            try (Statement st = connection.createStatement()) {
                if (parse != null) {
                    try (ResultSet rs = st.executeQuery(sql)) {
                        return parse.apply(new ResultSetWrapper(rs));
                    }
                } else {
                    st.executeUpdate(sql);
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new StorageException(sql, ex);
        }
    }

    protected void withPreparedStatement(String sql, Consumer<PreparedStatementWrapper> prepare) throws StorageException {
        withPreparedStatement(sql, prepare, null);
    }

    protected <R> R withPreparedStatement(String sql, Function<R> parse) throws StorageException {
        return withPreparedStatement(sql, null, parse);
    }

    protected <R> R withPreparedStatement(String sql, Consumer<PreparedStatementWrapper> prepare, Function<R> parse) throws StorageException {
        assert dataSource != null;

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement(sql)) {
                if (prepare != null) {
                    prepare.accept(new PreparedStatementWrapper<>(st));
                }

                if (parse != null) {
                    try (ResultSet rs = st.executeQuery()) {
                        return parse.apply(new ResultSetWrapper(rs));
                    }
                } else {
                    st.executeUpdate();
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new StorageException(sql, ex);
        }
    }

    protected void withCallableStatement(String sql, Consumer<CallableStatementWrapper> prepare, Consumer<CallableStatementWrapper> parse)
            throws StorageException {
        assert dataSource != null;

        try (Connection connection = dataSource.getConnection()) {
            try (CallableStatement st = connection.prepareCall(sql)) {
                CallableStatementWrapper<CallableStatement> wrapper = new CallableStatementWrapper<>(st);
                prepare.accept(wrapper);
                st.executeUpdate();
                parse.accept(wrapper);
            }
        } catch (SQLException ex) {
            throw new StorageException(sql, ex);
        }
    }

    @FunctionalInterface
    protected interface Consumer<T> {
        void accept(T st) throws SQLException;
    }

    @FunctionalInterface
    protected interface Function<R> {
        R apply(ResultSetWrapper rs) throws SQLException;
    }

    private static class Wrapper {
        int idx;
    }

    private static class StatementWrapper<S extends Statement> extends Wrapper {
        final S statement;

        private StatementWrapper(S statement) {
            this.statement = statement;
        }
    }

    protected static class PreparedStatementWrapper<PS extends PreparedStatement> extends StatementWrapper<PS> {

        private PreparedStatementWrapper(PS statement) {
            super(statement);
        }

        public void setLong(long x) throws SQLException {
            statement.setLong(++idx, x);
        }

        public void setString(String x) throws SQLException {
            statement.setString(++idx, x);
        }

        public void setDate(Date x) throws SQLException {
            statement.setDate(++idx, x);
        }

        public void setTime(Time x) throws SQLException {
            statement.setTime(++idx, x);
        }

        public void setTimestamp(Timestamp x) throws SQLException {
            statement.setTimestamp(++idx, x);
        }

        public void setArray(String typeName, Object[] elements) throws SQLException {
            Array array = ((OracleConnection) statement.getConnection()).createOracleArray(typeName, elements);
            statement.setArray(++idx, array);
        }

        public void setObject(Object x) throws SQLException {
            statement.setObject(++idx, x);
        }

        public void addBatch() throws SQLException {
            statement.addBatch();
        }

        public boolean execute() throws SQLException {
            return statement.execute();
        }

        public int executeUpdate() throws SQLException {
            return statement.executeUpdate();
        }

        public int[] executeBatch() throws SQLException {
            return statement.executeBatch();
        }

        public ResultSet executeQuery() throws SQLException {
            return statement.executeQuery();
        }
    }

    protected static class CallableStatementWrapper<CS extends CallableStatement> extends PreparedStatementWrapper<CS> {
        private int idxGet;

        private CallableStatementWrapper(CS statement) {
            super(statement);
        }

        public void registerOutParameter(int sqlType) throws SQLException {
            statement.registerOutParameter(++idx, sqlType);
        }

        public void registerOutParameter(int sqlType, String typeName) throws SQLException {
            statement.registerOutParameter(++idx, sqlType, typeName);
        }

        public long getLong() throws SQLException {
            return statement.getLong(++idxGet);
        }

        public Object getArray() throws SQLException {
            return statement.getArray(++idxGet).getArray();
        }
    }

    protected static class ResultSetWrapper extends Wrapper {
        private final ResultSet resultSet;

        private ResultSetWrapper(ResultSet resultSet) {
            this.resultSet = resultSet;
        }

        public boolean next() throws SQLException {
            idx = 0;
            return resultSet.next();
        }

        public short getShort() throws SQLException {
            return resultSet.getShort(++idx);
        }

        public int getInt() throws SQLException {
            return resultSet.getInt(++idx);
        }

        public long getLong() throws SQLException {
            return resultSet.getLong(++idx);
        }

        public String getString() throws SQLException {
            return resultSet.getString(++idx);
        }

        public Date getDate() throws SQLException {
            return resultSet.getDate(++idx);
        }

        public Time getTime() throws SQLException {
            return resultSet.getTime(++idx);
        }

        public Timestamp getTimestamp() throws SQLException {
            return resultSet.getTimestamp(++idx);
        }

        public boolean getBoolean() throws SQLException {
            return resultSet.getBoolean(++idx);
        }

        public boolean getYNBoolean() throws SQLException {
            return "Y".equals(resultSet.getString(++idx));
        }

        public Object getObject() throws SQLException {
            return resultSet.getObject(++idx);
        }
    }
}