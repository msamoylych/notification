package org.java.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by msamoylych on 25.04.2017.
 */
public final class StringUtils {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static String toString(byte[] bytes) {
        return new String(bytes, DEFAULT_CHARSET);
    }

    public static byte[] getBytes(String str) {
        return str.getBytes(DEFAULT_CHARSET);
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static String notNull(String s) {
        return s != null ? s : "";
    }

    public static boolean checkLength(String s, int max) {
        return checkLength(s, 0, max);
    }

    public static boolean checkLength(String s, int min, int max) {
        if (s == null) {
            return true;
        }
        int l = s.length();
        return l >= min && l <= max;
    }

    public static String repeatWithDelimiter(String str, String delimiter, int count) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < count - 1; i++) {
            sb.append(delimiter).append(str);
        }
        return sb.toString();
    }
}
