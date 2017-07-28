package org.java.utils.storage;

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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by msamoylych on 09.06.2017.
 */
@Component
@SuppressWarnings("unused")
public class PreloadStorageController extends Storage implements ApplicationContextAware, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreloadStorageController.class);

    private static final String SELECT_DATE = "SELECT sys_extract_utc(systimestamp) FROM dual";
    private static final String CHECK = "SELECT code, changed FROM CHANGES WHERE changed > ?";

    private final Scheduler scheduler;

    private final Map<String, PreloadStorage> storages = new HashMap<>();
    private Timestamp timestamp;

    private ApplicationContext applicationContext;

    public PreloadStorageController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initTimestamp();

        BeanUtils.forEachBeanOfType(applicationContext, PreloadStorage.class, preloadStorage -> {
            storages.put(preloadStorage.code(), preloadStorage);
            preloadStorage.load();
        });

        scheduler.scheduleTask(this::check, 300, 60, TimeUnit.SECONDS);
    }

    private void check() {
        try {
            withPreparedStatement(CHECK, st -> st.set(timestamp), rs -> {
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
            LOGGER.error("Check changes failed", ex);
        }
    }

    private void initTimestamp() throws StorageException {
        timestamp = withStatement(SELECT_DATE,
                rs -> {
                    if (rs.next()) {
                        return rs.getTimestamp();
                    } else {
                        throw new IllegalStateException("Can't init timestamp");
                    }
                }
        );
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}