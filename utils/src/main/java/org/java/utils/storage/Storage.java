package org.java.utils.storage;

import oracle.jdbc.OracleConnection;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.java.utils.properties.PropertiesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Objects;

/**
 * Created by msamoylych on 06.06.2017.
 */
public abstract class Storage {
    private static final Logger LOGGER = LoggerFactory.getLogger(Storage.class);

    private static final String FACTORY_CLASS = "oracle.jdbc.pool.OracleDataSource";

    private static final String URL = "url";
    private static final String USER = "user";
    private static final String PASSWORD = "password";

    private static final DataSource DATA_SOURCE;

    static {
        try {
            LOGGER.info("Initialize data source...");
            PoolDataSource dataSource = PoolDataSourceFactory.getPoolDataSource();
            dataSource.setConnectionFactoryClassName(FACTORY_CLASS);

            dataSource.setURL(PropertiesProvider.get(URL));
            dataSource.setUser(PropertiesProvider.get(USER));
            dataSource.setPassword(PropertiesProvider.get(PASSWORD));

            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                LOGGER.info("Database: {}", metaData.getDatabaseProductVersion());
                LOGGER.info("URL: {}", metaData.getURL());
                LOGGER.info("User: {}", metaData.getUserName());
            }

            DATA_SOURCE = dataSource;
        } catch (SQLException ex) {
            throw new IllegalStateException("Can't initialize data source", ex);
        }
    }

    protected static void withStatement(String sql) throws StorageException {
        withStatement(sql, null);
    }

    protected static void withStatement(String sql, VoidFunction parse) throws StorageException {
        withStatement(sql, (Function<Void>) parse);
    }

    protected static <R> R withStatement(String sql, Function<R> parse) throws StorageException {
        try (Connection connection = DATA_SOURCE.getConnection()) {
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

    protected static void withPreparedStatement(String sql, Consumer<PreparedStatementWrapper<?>> prepare) throws StorageException {
        withPreparedStatement(sql, prepare, null);
    }

    protected static <R> R withPreparedStatement(String sql, Function<R> parse) throws StorageException {
        return withPreparedStatement(sql, null, parse);
    }

    protected static void withPreparedStatement(String sql, VoidFunction parse) throws StorageException {
        withPreparedStatement(sql, null, parse);
    }

    protected static <R> R withPreparedStatement(String sql, Consumer<PreparedStatementWrapper<?>> prepare, Function<R> parse) throws StorageException {
        try (Connection connection = DATA_SOURCE.getConnection()) {
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

    protected static void withCallableStatement(String sql, Consumer<CallableStatementWrapper<?>> prepare, Consumer<CallableStatementWrapper<?>> parse) throws StorageException {
        try (Connection connection = DATA_SOURCE.getConnection()) {
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
    protected interface Consumer<T extends PreparedStatementWrapper<?>> {
        void accept(T st) throws SQLException;
    }

    @FunctionalInterface
    protected interface Function<R> {
        R apply(ResultSetWrapper rs) throws SQLException;
    }

    @FunctionalInterface
    protected interface VoidFunction extends Function<Void> {
        void voidApply(ResultSetWrapper rs) throws SQLException;

        default Void apply(ResultSetWrapper rs) throws SQLException {
            voidApply(rs);
            return null;
        }
    }

    private static class StatementWrapper<S extends Statement> {
        final S statement;
        int set;

        private StatementWrapper(S statement) {
            this.statement = statement;
        }
    }

    protected static class PreparedStatementWrapper<PS extends PreparedStatement> extends StatementWrapper<PS> {

        private PreparedStatementWrapper(PS statement) {
            super(statement);
        }

        public void set(long x) throws SQLException {
            statement.setLong(++set, x);
        }

        public void set(String x) throws SQLException {
            statement.setString(++set, x);
        }

        public void set(Enum<?> x) throws SQLException {
            set(x.name());
        }

        public void set(Entity x) throws SQLException {
            set(x.id());
        }

        public void set(Timestamp x) throws SQLException {
            statement.setTimestamp(++set, x);
        }

        public void set(String typeName, Object[] elements) throws SQLException {
            Array array = ((OracleConnection) statement.getConnection()).createOracleArray(typeName, elements);
            statement.setArray(++set, array);
        }
    }

    protected static class CallableStatementWrapper<CS extends CallableStatement> extends PreparedStatementWrapper<CS> {
        private int get;

        private CallableStatementWrapper(CS statement) {
            super(statement);
        }

        public void registerOutParameter(int sqlType) throws SQLException {
            statement.registerOutParameter(++set, sqlType);
        }

        public void registerOutParameter(int sqlType, String typeName) throws SQLException {
            statement.registerOutParameter(++set, sqlType, typeName);
        }

        public long getLong() throws SQLException {
            return statement.getLong(++get);
        }

        public String getString() throws SQLException {
            return statement.getString(++get);
        }

        public Object getArray() throws SQLException {
            return statement.getArray(++get).getArray();
        }
    }

    protected static class ResultSetWrapper {
        private final ResultSet resultSet;
        private int get;

        private ResultSetWrapper(ResultSet resultSet) {
            this.resultSet = resultSet;
        }

        public boolean next() throws SQLException {
            get = 0;
            return resultSet.next();
        }

        public int getInt() throws SQLException {
            return resultSet.getInt(++get);
        }

        public long getLong() throws SQLException {
            return resultSet.getLong(++get);
        }

        public String getString() throws SQLException {
            return resultSet.getString(++get);
        }

        public String getString(int columnIndex) throws SQLException {
            return resultSet.getString(columnIndex);
        }

        public <E extends Enum<E>> E getEnum(Class<E> enumType) throws SQLException {
            return Enum.valueOf(enumType, getString());
        }

        public Timestamp getTimestamp() throws SQLException {
            return resultSet.getTimestamp(++get);
        }

        public boolean getYNBoolean() throws SQLException {
            return Objects.equals(resultSet.getString(++get), "Y");
        }
    }
}