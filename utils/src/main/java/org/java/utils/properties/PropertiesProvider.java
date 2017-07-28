package org.java.utils.properties;

/**
 * Created by msamoylych on 21.07.2017.
 */
public final class PropertiesProvider {

    public static String get(String name) {
        return System.getProperty(name);
    }

    public static String get(String name, String def) {
        String value = get(name);
        return value != null ? value : def;
    }
}
