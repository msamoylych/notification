package org.java.utils;

/**
 * Created by msamoylych on 12.04.2017.
 */
public class Json {

    private static final char START_BRACE = '{';
    private static final char END_BRACE = '}';
    private static final char START_BRACKET = '[';
    private static final char END_BRACKET = ']';
    private static final char QUOTE = '"';
    private static final char COLON = ':';
    private static final char COMMA = ',';

    public static JsonBuilder start() {
        return new JsonBuilder();
    }

    public static String json(String name, String value) {
        return "" + START_BRACE + QUOTE + name + QUOTE + COLON + QUOTE + value + QUOTE + END_BRACE;
    }

    public static class JsonBuilder {

        private final StringBuilder builder = new StringBuilder();
        private boolean addComma;

        private JsonBuilder() {
            builder.append(START_BRACE);
        }

        public JsonBuilder add(String name, String value) {
            if (value == null) {
                return this;
            }
            if (addComma) {
                builder.append(COMMA);
            }
            builder.append(QUOTE).append(name).append(QUOTE).append(COLON).append(QUOTE).append(value).append(QUOTE);
            addComma = true;
            return this;
        }

        public JsonBuilder add(String name, Number value) {
            if (value == null) {
                return this;
            }
            if (addComma) {
                builder.append(COMMA);
            }
            builder.append(QUOTE).append(name).append(QUOTE).append(COLON).append(value);
            addComma = true;
            return this;
        }

        public JsonBuilder addArray(String name, String... values) {
            if (addComma) {
                builder.append(COMMA);
            }
            builder.append(QUOTE).append(name).append(QUOTE).append(COLON).append(START_BRACKET);
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    builder.append(COMMA);
                }
                builder.append(QUOTE).append(values[i]).append(QUOTE);
            }
            builder.append(END_BRACKET);
            addComma = true;
            return this;
        }

        public JsonBuilder startArray(String name) {
            if (addComma) {
                builder.append(COMMA);
            }
            builder.append(QUOTE).append(name).append(QUOTE).append(COLON).append(START_BRACKET);
            addComma = false;
            return this;
        }

        public JsonBuilder endArray() {
            builder.append(END_BRACKET);
            addComma = true;
            return this;
        }

        public JsonBuilder startObject() {
            if (addComma) {
                builder.append(COMMA);
            }
            builder.append(START_BRACE);
            addComma = false;
            return this;
        }

        public JsonBuilder startObject(String name) {
            if (addComma) {
                builder.append(COMMA);
            }
            builder.append(QUOTE).append(name).append(QUOTE).append(COLON).append(START_BRACE);
            addComma = false;
            return this;
        }

        public JsonBuilder endObject() {
            builder.append(END_BRACE);
            addComma = true;
            return this;
        }

        public String end() {
            builder.append(END_BRACE);
            return builder.toString();
        }
    }
}