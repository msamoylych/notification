package org.java.notification.storage;

import oracle.jdbc.OracleConnection;
import org.java.utils.provider.DataSourceProvider;

import java.sql.*;
import java.util.Objects;

/**
 * Created by msamoylych on 06.06.2017.
 */
public abstract class Storage {

    protected void withStatement(String sql) throws StorageException {
        withStatement(sql, null);
    }

    protected <R> R withStatement(String sql, Function<R> parse) throws StorageException {
        try (Connection connection = DataSourceProvider.DATA_SOURCE.getConnection()) {
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

    protected void withPreparedStatement(String sql, Consumer<PreparedStatementWrapper<?>> prepare) throws StorageException {
        withPreparedStatement(sql, prepare, null);
    }

    protected <R> R withPreparedStatement(String sql, Function<R> parse) throws StorageException {
        return withPreparedStatement(sql, null, parse);
    }

    protected <R> R withPreparedStatement(String sql, Consumer<PreparedStatementWrapper<?>> prepare, Function<R> parse) throws StorageException {
        try (Connection connection = DataSourceProvider.DATA_SOURCE.getConnection()) {
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

    protected void withCallableStatement(String sql, Consumer<CallableStatementWrapper<?>> prepare, Consumer<CallableStatementWrapper<?>> parse) throws StorageException {
        try (Connection connection = DataSourceProvider.DATA_SOURCE.getConnection()) {
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
    protected interface Consumer<T extends StatementWrapper<?>> {
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

        public void setTimestamp(Timestamp x) throws SQLException {
            statement.setTimestamp(++idx, x);
        }

        public void setArray(String typeName, Object[] elements) throws SQLException {
            Array array = ((OracleConnection) statement.getConnection()).createOracleArray(typeName, elements);
            statement.setArray(++idx, array);
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

        public int getInt() throws SQLException {
            return resultSet.getInt(++idx);
        }

        public long getLong() throws SQLException {
            return resultSet.getLong(++idx);
        }

        public String getString() throws SQLException {
            return resultSet.getString(++idx);
        }

        public <E extends Enum<E>> E getEnum(Class<E> enumType) throws SQLException {
            return Enum.valueOf(enumType, getString());
        }

        public String getString(int columnIndex) throws SQLException {
            return resultSet.getString(columnIndex);
        }

        public Timestamp getTimestamp() throws SQLException {
            return resultSet.getTimestamp(++idx);
        }

        public boolean getYNBoolean() throws SQLException {
            return Objects.equals(resultSet.getString(++idx), "Y");
        }
    }
}