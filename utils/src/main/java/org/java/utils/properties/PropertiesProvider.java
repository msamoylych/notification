package org.java.utils.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by msamoylych on 21.07.2017.
 */
public final class PropertiesProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesProvider.class);

    private static final Properties PROPERTIES = new Properties();

    static {
        String filePath = System.getProperty("properties", "application.properties");
        Path path = Paths.get(filePath);
        try (Reader reader = Files.newBufferedReader(path)) {
            LOGGER.info("Properties file: {}", path.toAbsolutePath());
            PROPERTIES.load(reader);
        } catch (IOException e) {
            // ignore
        }
    }

    public static String get(String name) {
        String property = System.getProperty(name);
        return property != null ? property : PROPERTIES.getProperty(name);
    }

    public static String get(String name, String def) {
        String value = get(name);
        return value != null ? value : def;
    }

    public static Integer getInteger(String name) {
        try {
            String value = get(name);
            return value != null ? Integer.parseInt(value) : null;
        } catch (Exception ex) {
            LOGGER.error("Property '{}' has invalid value");
            return null;
        }
    }

    public static int getInteger(String name, int def) {
        Integer value = getInteger(name);
        return value != null ? value : def;
    }
}
