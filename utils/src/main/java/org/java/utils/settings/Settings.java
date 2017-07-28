package org.java.utils.settings;

import org.java.utils.storage.Storage;
import org.java.utils.storage.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by msamoylych on 21.07.2017.
 */
final class Settings extends Storage {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsRegister.class);

    private static final String SELECT_SETTINGS = "SELECT code, value FROM SETTINGS";
    private static final Set<Setting<?>> SETTINGS = new HashSet<>();
    private static final Map<String, String> VALUES = new HashMap<>();

    static {
        try {
            LOGGER.info("Initialize settings...");
            withStatement(SELECT_SETTINGS, rs -> {
                while (rs.next()) {
                    VALUES.put(rs.getString(), rs.getString());
                }
            });
        } catch (StorageException ex) {
            throw new IllegalStateException("Can't initialize settings", ex);
        }
    }

    static int setting(String code, String name, int defaultValue) {
        return add(new IntegerSetting(code, name, defaultValue));
    }

    static String setting(String code, String name, String defaultValue) {
        return add(new StringSetting(code, name, defaultValue));
    }

    static boolean setting(String code, String name, boolean defaultValue) {
        return add(new BooleanSetting(code, name, defaultValue));
    }

    private static <T> T add(Setting<T> setting) {
        if (SETTINGS.add(setting)) {
            return setting.value();
        } else {
            throw new IllegalStateException("Duplicate setting with code " + setting.code);
        }
    }

    private static abstract class Setting<T> {
        private final String code;
        private final String name;
        private final T defaultValue;

        private Setting(String code, String name, T defaultValue) {
            this.code = code;
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public String code() {
            return code;
        }

        public String name() {
            return name;
        }

        public T value() {
            String s = VALUES.get(code);
            return s != null ? parse(s) : defaultValue;
        }

        abstract T parse(String s);
    }

    private static class IntegerSetting extends Setting<Integer> {
        private IntegerSetting(String code, String name, Integer value) {
            super(code, name, value);
        }

        @Override
        Integer parse(String s) {
            return Integer.parseInt(s);
        }
    }

    private static class StringSetting extends Setting<String> {
        private StringSetting(String code, String name, String value) {
            super(code, name, value);
        }

        @Override
        String parse(String s) {
            return s;
        }
    }

    private static class BooleanSetting extends Setting<Boolean> {
        private BooleanSetting(String code, String name, Boolean value) {
            super(code, name, value);
        }

        @Override
        Boolean parse(String s) {
            return Boolean.parseBoolean(s);
        }
    }
}
