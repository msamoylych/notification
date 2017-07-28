package org.java.utils.settings;

/**
 * Created by msamoylych on 24.07.2017.
 */
public interface SettingsRegister {

    default int setting(String code, String name, int defaultValue) {
        return Settings.setting(code, name, defaultValue);
    }

    default String setting(String code, String name, String defaultValue) {
        return Settings.setting(code, name, defaultValue);
    }

    default boolean setting(String code, String name, boolean defaultValue) {
        return Settings.setting(code, name, defaultValue);
    }
}