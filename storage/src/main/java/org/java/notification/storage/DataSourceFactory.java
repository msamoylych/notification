package org.java.notification.storage;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * Created by msamoylych on 04.05.2017.
 */
@Component
final class DataSourceFactory implements FactoryBean<DataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceFactory.class);

    @Override
    public DataSource getObject() throws Exception {
        PoolDataSource dataSource = PoolDataSourceFactory.getPoolDataSource();
        dataSource.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");

        dataSource.setURL("jdbc:oracle:thin:@//localhost:2483/orcl");
        dataSource.setUser("notification");
        dataSource.setPassword("notification");

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            LOGGER.info(metaData.getDatabaseProductVersion());
            LOGGER.info("URL: {}", metaData.getURL());
            LOGGER.info("User: {}", metaData.getUserName());
        } catch (Throwable th) {
            throw new IllegalStateException("DataSource is unavailable", th);
        }

        return dataSource;
    }

    @Override
    public Class<?> getObjectType() {
        return DataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}