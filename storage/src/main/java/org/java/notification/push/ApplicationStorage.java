package org.java.notification.push;

import org.java.notification.push.application.*;
import org.java.notification.storage.PreloadStorage;
import org.java.notification.storage.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

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
            "SELECT s.CODE, a.PNS, a.ID, a.PACKAGE_NAME, an.SERVER_KEY " +
                    "FROM SYSTEM_APPLICATION sa " +
                    "LEFT JOIN SYSTEM s ON s.ID = sa.SYSTEM_ID " +
                    "LEFT JOIN APPLICATION a ON a.ID = sa.APPLICATION_ID " +
                    "LEFT JOIN APPLICATION_FCM an ON an.ID = a.ID";

    private final Map<Long, Application> applicationsById = new HashMap<>();
    private final Map<String, Map<PNS, Map<String, Application>>> applications = new HashMap<>();

    private final Map<String, Map<PNS, Application>> systemPNSDefaultApplications = new HashMap<>();
    private final Map<String, Map<String, Application>> systemPackageDefaultApplications = new HashMap<>();
    private final Map<String, Application> systemDefaultApplications = new HashMap<>();

    public Application application(Long applicationId) {
        return get(() -> applicationsById.get(applicationId));
    }

    public Application application(String systemCode, PNS pns, String packageName) {
        return get(() -> {
            if (pns != null && packageName != null) {
                Map<PNS, Map<String, Application>> systemApplications = applications.get(systemCode);
                Map<String, Application> apps = systemApplications != null ? systemApplications.get(pns) : null;
                return apps != null ? apps.get(packageName) : null;
            } else if (pns != null) {
                Map<PNS, Application> apps = systemPNSDefaultApplications.get(systemCode);
                return apps != null ? apps.get(pns) : null;
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

    private void selectApplications() throws StorageException {
        withPreparedStatement(SELECT, rs -> {
            applicationsById.clear();
            applications.clear();

            while (rs.next()) {
                String systemCode = rs.getString();
                PNS pns = PNS.valueOf(rs.getString());
                Long applicationId = rs.getLong();
                Application application;
                switch (pns) {
                    case FCM:
                        application = new FCMApplication(rs.getString(5));
                        break;
                    case APNs:
                        application = new APNsApplication();
                        break;
                    case WNS:
                        application = new WNSApplication();
                        break;
                    case MPNS:
                        application = new MPNSApplication();
                        break;
                    default:
                        LOGGER.error("Application with id <{}> has unknown PNS: {}", applicationId, pns);
                        continue;
                }
                application.id(applicationId);
                application.packageName(rs.getString());

                applicationsById.put(applicationId, application);
                applications
                        .computeIfAbsent(systemCode, s -> new EnumMap<>(PNS.class))
                        .computeIfAbsent(application.pns(), aPns -> new HashMap<>())
                        .put(application.packageName(), application);
            }

            return null;
        });
    }

    private void prepareApplication() {
        systemPNSDefaultApplications.clear();
        systemPackageDefaultApplications.clear();
        systemDefaultApplications.clear();

        for (Map.Entry<String, Map<PNS, Map<String, Application>>> systemApplications : applications.entrySet()) {
            String systemCode = systemApplications.getKey();
            Application defaultApplication = null;
            boolean one = true;

            Map<PNS, Map<String, Application>> systemApplicationsByPNS = systemApplications.getValue();
            for (Map.Entry<PNS, Map<String, Application>> applicationsByPNS : systemApplicationsByPNS.entrySet()) {
                Map<String, Application> applicationsByPackage = applicationsByPNS.getValue();
                if (systemApplicationsByPNS.size() == 1) {
                    systemPackageDefaultApplications.put(systemCode, applicationsByPackage);
                }

                for (Map.Entry<String, Application> application : applicationsByPackage.entrySet()) {
                    Application app = application.getValue();
                    if (applicationsByPackage.size() == 1) {
                        systemPNSDefaultApplications
                                .computeIfAbsent(systemCode, s -> new HashMap<>())
                                .put(applicationsByPNS.getKey(), app);
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