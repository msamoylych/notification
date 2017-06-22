package org.java.notification.storage;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Created by msamoylych on 04.05.2017.
 */
@Component
final class DataSourceFactory implements FactoryBean<DataSource> {

    @Override
    public DataSource getObject() throws Exception {
        PoolDataSource dataSource = PoolDataSourceFactory.getPoolDataSource();
        dataSource.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");

        dataSource.setURL("jdbc:oracle:thin:@//localhost:2483/orclpdb1");
        dataSource.setUser("notification");
        dataSource.setPassword("notification");

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