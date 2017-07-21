package org.java.utils;

import org.java.utils.provider.DataSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by msamoylych on 21.07.2017.
 */
public final class Settings {
    private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);

    public static final BooleanSetting WS_RECEIVER_ENABLED = setting("WS_RECEIVER_ENABLED", "Запуск WS сервера", false);
    public static final IntegerSetting WS_RECEIVER_PORT = setting("WS_RECEIVER_PORT", "Порт WS сервера", 8787);
    public static final StringSetting WS_RECEIVER_PATH = setting("WS_RECEIVER_PATH", "Путь WS сервера", "ws");

    private static final String SELECT_SETTINGS = "SELECT code, value FROM SETTINGS";

    static void init() throws Exception {
        Field[] fields = Settings.class.getFields();
        Map<String, Setting<?>> settings = new HashMap<>(fields.length);
        for (Field field : fields) {
            Setting<?> setting = (Setting<?>) field.get(null);
            settings.put(setting.code, setting);
        }

        try (Connection connection = DataSourceProvider.DATA_SOURCE.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rs = statement.executeQuery(SELECT_SETTINGS)) {
                    while (rs.next()) {
                        String code = rs.getString(1);
                        Setting<?> setting = settings.get(code);
                        if (setting != null) {
                            setting.value(rs);
                        }
                    }
                }
            }
        }
    }

    private static IntegerSetting setting(String code, String name, int defaultValue) {
        return new IntegerSetting(code, name, defaultValue);
    }

    private static StringSetting setting(String code, String name, String defaultValue) {
        return new StringSetting(code, name, defaultValue);
    }

    private static BooleanSetting setting(String code, String name, boolean defaultValue) {
        return new BooleanSetting(code, name, defaultValue);
    }

    private static abstract class Setting<T> {
        private String code;
        private String name;
        protected T value;

        private Setting(String code, String name, T defaultValue) {
            this.code = code;
            this.name = name;
            this.value = defaultValue;
        }

        protected abstract void value(ResultSet rs) throws SQLException;

        public String name() {
            return name;
        }

        public T value() {
            return value;
        }
    }

    private static class IntegerSetting extends Setting<Integer> {
        private IntegerSetting(String code, String name, Integer value) {
            super(code, name, value);
        }

        @Override
        protected void value(ResultSet rs) throws SQLException {
            value = rs.getInt(2);
        }
    }

    private static class StringSetting extends Setting<String> {
        private StringSetting(String code, String name, String value) {
            super(code, name, value);
        }

        @Override
        protected void value(ResultSet rs) throws SQLException {
            value = rs.getString(2);
        }
    }

    private static class BooleanSetting extends Setting<Boolean> {
        private BooleanSetting(String code, String name, Boolean value) {
            super(code, name, value);
        }

        @Override
        protected void value(ResultSet rs) throws SQLException {
            value = rs.getBoolean(2);
        }
    }
}
