package org.java.notification.push;

import org.java.notification.push.application.*;
import org.java.notification.storage.PreloadStorage;
import org.java.notification.storage.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by msamoylych on 05.05.2017.
 */
@Repository
public class ApplicationStorage extends PreloadStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStorage.class);

    private static final String CODE = "APPLICATION";
    private static final String SELECT =
            "SELECT s.CODE, a.ID, a.OS, an.SERVER_KEY, a.PACKAGE_NAME " +
                    "FROM SYSTEM_APPLICATION sa " +
                    "LEFT JOIN SYSTEM s ON s.ID = sa.SYSTEM_ID " +
                    "LEFT JOIN APPLICATION a ON a.ID = sa.APPLICATION_ID " +
                    "LEFT JOIN APPLICATION_ANDROID an ON an.ID = a.ID";

    private final DataSource dataSource;

    private final Map<String, Map<OS, Map<String, Application>>> applications = new HashMap<>();
    private final Map<Long, Application> applicationsById = new HashMap<>();

    private final Map<String, Map<OS, Application>> systemOSDefaultApplications = new HashMap<>();
    private final Map<String, Map<String, Application>> systemPackageDefaultApplications = new HashMap<>();
    private final Map<String, Application> systemDefaultApplications = new HashMap<>();

    public ApplicationStorage(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Application application(Long applicationId) {
        return get(() -> applicationsById.get(applicationId));
    }

    public Application application(String systemCode, OS os, String packageName) {
        return get(() -> {
            if (os != null && packageName != null) {
                Map<OS, Map<String, Application>> systemApplications = applications.get(systemCode);
                Map<String, Application> apps = systemApplications != null ? systemApplications.get(os) : null;
                return apps != null ? apps.get(packageName) : null;
            } else if (os != null) {
                Map<OS, Application> apps = systemOSDefaultApplications.get(systemCode);
                return apps != null ? apps.get(os) : null;
            } else if (packageName != null) {
                Map<String, Application> apps = systemPackageDefaultApplications.get(systemCode);
                return apps != null ? apps.get(packageName) : null;
            } else {
                return systemDefaultApplications.get(systemCode);
            }
        });
    }

    @Override
    public String code() {
        return CODE;
    }

    @Override
    protected void doLoad() throws StorageException {
        selectApplications();
        prepareApplication();
    }

    private void selectApplications() {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement(SELECT)) {
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        String systemCode = rs.getString(1);
                        Long applicationId = rs.getLong(2);
                        Application application = applicationsById.get(applicationId);
                        if (application == null) {
                            OS os = OS.valueOf(rs.getString(3));
                            switch (os) {
                                case ANDROID:
                                    application = new FCMApplication();
                                    ((FCMApplication) application).serverKey(rs.getString(4));
                                    break;
                                case IOS:
                                    application = new APNsApplication();
                                    break;
                                case WINDOWS:
                                    application = new WNSApplication();
                                    break;
                                case WINDOWS_PHONE:
                                    application = new MPNSApplication();
                                    break;
                                default:
                                    throw new IllegalStateException("Unknown OS: " + os);
                            }
                            application.id(applicationId);
                            application.packageName(rs.getString(5));
                            applicationsById.put(applicationId, application);
                        }

                        applications
                                .computeIfAbsent(systemCode, s -> new EnumMap<>(OS.class))
                                .computeIfAbsent(application.os(), os -> new HashMap<>())
                                .put(application.packageName(), application);
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Can't load applications", ex);
        }
    }

    private void prepareApplication() {
        for (Map.Entry<String, Map<OS, Map<String, Application>>> systemApplications : applications.entrySet()) {
            String systemCode = systemApplications.getKey();
            Application defaultApplication = null;
            boolean one = true;

            Map<OS, Map<String, Application>> systemApplicationsByOS = systemApplications.getValue();
            for (Map.Entry<OS, Map<String, Application>> applicationsByOS : systemApplicationsByOS.entrySet()) {
                Map<String, Application> applicationsByPackage = applicationsByOS.getValue();
                if (systemApplicationsByOS.size() == 1) {
                    systemPackageDefaultApplications.put(systemCode, applicationsByPackage);
                }

                for (Map.Entry<String, Application> application : applicationsByPackage.entrySet()) {
                    Application app = application.getValue();
                    if (applicationsByPackage.size() == 1) {
                        systemOSDefaultApplications
                                .computeIfAbsent(systemCode, s -> new HashMap<>())
                                .put(applicationsByOS.getKey(), app);
                    }

                    if (defaultApplication == null) {
                        defaultApplication = app;
                    } else {
                        one = false;
                    }
                }
            }

            if (one) {
                systemDefaultApplications.put(systemCode, defaultApplication);
            }
        }
    }
}