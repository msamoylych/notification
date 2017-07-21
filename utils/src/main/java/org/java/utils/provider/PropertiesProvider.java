package org.java.utils.provider;

/**
 * Created by msamoylych on 21.07.2017.
 */
public final class PropertiesProvider {

    public static String get(String name) {
        return System.getProperty(name);
    }
}
