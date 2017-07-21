package org.java.utils.provider;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Created by msamoylych on 21.07.2017.
 */
public class DataSourceProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceProvider.class);

    private static final String FACTORY_CLASS = "oracle.jdbc.pool.OracleDataSource";
    private static final String URL = "url";
    private static final String USER = "user";
    private static final String PASSWORD = "password";

    public static final DataSource DATA_SOURCE;

    static {
        try {
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
}
