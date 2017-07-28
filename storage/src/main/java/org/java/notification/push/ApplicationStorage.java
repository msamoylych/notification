package org.java.notification.push;

import org.java.notification.push.application.*;
import org.java.utils.storage.PreloadStorage;
import org.java.utils.storage.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Created by msamoylych on 05.05.2017.
 */
@Repository
public class ApplicationStorage extends PreloadStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStorage.class);

    private static final String CODE = "APPLICATION";
    private static final String SELECT =
            "SELECT s.id, a.id, a.pns, a.package_name, fcm.server_key " +
                    "FROM SYSTEM_APPLICATION sa " +
                    "LEFT JOIN SYSTEM s ON s.id = sa.system_id " +
                    "LEFT JOIN APPLICATION a ON a.id = sa.application_id " +
                    "RIGHT JOIN APPLICATION_FCM fcm ON fcm.id = a.id";

    private final Map<Long, Application> applicationsById = new HashMap<>();

    private final Map<Long, Map<PNS, Map<String, Application>>> applications = new HashMap<>();

    private final Map<Long, Map<PNS, Application>> systemPNSApplications = new HashMap<>();
    private final Map<Long, Map<String, Application>> systemPackageApplications = new HashMap<>();
    private final Map<Long, Application> systemApplications = new HashMap<>();

    @Override
    public String code() {
        return CODE;
    }

    public Application application(Long applicationId) {
        try {
            read.lock();
            return applicationsById.get(applicationId);
        } finally {
            read.unlock();
        }
    }

    public Application application(Long systemId, PNS pns, String packageName) {
        try {
            read.lock();

            if (pns != null && packageName != null) {
                Map<PNS, Map<String, Application>> systemApplications = applications.get(systemId);
                Map<String, Application> apps = systemApplications != null ? systemApplications.get(pns) : null;
                return apps != null ? apps.get(packageName) : null;
            } else if (pns != null) {
                Map<PNS, Application> apps = systemPNSApplications.get(systemId);
                return apps != null ? apps.get(pns) : null;
            } else if (packageName != null) {
                Map<String, Application> apps = systemPackageApplications.get(systemId);
                return apps != null ? apps.get(packageName) : null;
            } else {
                return systemApplications.get(systemId);
            }
        } finally {
            read.unlock();
        }
    }

    @Override
    protected void doLoad() throws StorageException {
        List<Application> applications = selectApplications();
        try {
            write.lock();
            prepareApplications(applications);
        } finally {
            write.unlock();
        }
    }

    private List<Application> selectApplications() throws StorageException {
        return withPreparedStatement(SELECT, rs -> {
            List<Application> applications = new ArrayList<>();

            while (rs.next()) {
                Long systemId = rs.getLong();
                Long applicationId = rs.getLong();
                PNS pns = PNS.valueOf(rs.getString());
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
                        LOGGER.error("Application [id:{}] - unknown PNS: {}", applicationId, pns);
                        continue;
                }
                application.id(applicationId);
                application.systemId(systemId);
                application.packageName(rs.getString());

                applications.add(application);
            }

            return applications;
        });
    }

    private void prepareApplications(List<Application> apps) {
        applicationsById.clear();
        applications.clear();

        systemPNSApplications.clear();
        systemPackageApplications.clear();
        systemApplications.clear();

        for (Application app : apps) {
            applicationsById.put(app.id(), app);
            applications
                    .computeIfAbsent(app.systemId(), s -> new EnumMap<>(PNS.class))
                    .computeIfAbsent(app.pns(), pns -> new HashMap<>())
                    .put(app.packageName(), app);
        }

        for (Map.Entry<Long, Map<PNS, Map<String, Application>>> applications : applications.entrySet()) {
            Long systemId = applications.getKey();
            Application systemApplication = null;
            boolean one = true;

            Map<PNS, Map<String, Application>> systemApplicationsByPNS = applications.getValue();
            for (Map.Entry<PNS, Map<String, Application>> applicationsByPNS : systemApplicationsByPNS.entrySet()) {
                Map<String, Application> applicationsByPackage = applicationsByPNS.getValue();
                if (systemApplicationsByPNS.size() == 1) {
                    systemPackageApplications.put(systemId, applicationsByPackage);
                }

                for (Map.Entry<String, Application> application : applicationsByPackage.entrySet()) {
                    Application app = application.getValue();
                    if (applicationsByPackage.size() == 1) {
                        systemPNSApplications
                                .computeIfAbsent(systemId, s -> new HashMap<>())
                                .put(applicationsByPNS.getKey(), app);
                    }

                    if (systemApplication == null) {
                        systemApplication = app;
                    } else {
                        one = false;
                    }
                }
            }

            if (one) {
                systemApplications.put(systemId, systemApplication);
            }
        }
    }
}