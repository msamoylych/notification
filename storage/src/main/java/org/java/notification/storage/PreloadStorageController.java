package org.java.notification.storage;

import org.java.utils.BeanUtils;
import org.java.utils.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by msamoylych on 09.06.2017.
 */
@Component
public class PreloadStorageController extends Storage implements ApplicationContextAware, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreloadStorageController.class);

    private static final String SELECT_DATE = "SELECT sys_extract_utc(systimestamp) FROM dual";
    private static final String CHECK = "SELECT code, changed FROM CHANGES WHERE changed > ?";

    private final Scheduler scheduler;

    private Map<String, PreloadStorage> storages = new HashMap<>();
    private Timestamp timestamp;

    private ApplicationContext applicationContext;

    public PreloadStorageController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        BeanUtils.forEachBeanOfType(applicationContext, PreloadStorage.class, preloadStorage -> {
            storages.put(preloadStorage.code(), preloadStorage);
            preloadStorage.load();
        });

        initTimestamp();

        scheduler.scheduleTask(this::check, 300, 60, TimeUnit.SECONDS);
    }

    private void check() {
        try {
            withPreparedStatement(CHECK, st -> st.setTimestamp(timestamp), rs -> {
                while (rs.next()) {
                    storages.get(rs.getString()).load();
                    Timestamp t = rs.getTimestamp();
                    if (t.after(timestamp)) {
                        timestamp = t;
                    }
                }
                return null;
            });
        } catch (StorageException ex) {
            LOGGER.error("Check changes exception", ex);
        }
    }

    private void initTimestamp() {
        try {
            timestamp = withStatement(SELECT_DATE,
                    rs -> rs.next() ? rs.getTimestamp() : Timestamp.from(Instant.now())
            );
        } catch (StorageException ex) {
            LOGGER.error("Timestamp init exception", ex);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}