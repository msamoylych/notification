package org.java.utils.http;

import org.java.utils.file.Extension;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by msamoylych on 21.04.2017.
 */
@SuppressWarnings("WeakerAccess")
public final class ContentType {

    private static final String CHARSET = "; charset=UTF-8";

    public static final String TEXT_HTML = "text/html" + CHARSET;
    public static final String TEXT_CSS = "text/css" + CHARSET;
    public static final String TEXT_JS = "text/javascript" + CHARSET;

    public static final String IMAGE_PNG = "image/png";
    public static final String IMAGE_SVG = "image/svg+xml";
    public static final String IMAGE_ICO = "image/x-icon";

    public static final String APPLICATION_EOT = "application/vnd.ms-fontobject";
    public static final String APPLICATION_TTF = "application/x-font-truetype";
    public static final String APPLICATION_WOFF = "application/font-woff";
    public static final String APPLICATION_WOFF2 = "application/font-woff2";

    public static final String APPLICATION_FORM = "application/x-www-form-urlencoded" + CHARSET;
    public static final String APPLICATION_JSON = "application/json" + CHARSET;

    private static final Map<String, String> MAP = new LinkedHashMap<>();

    static {
        MAP.put(Extension.HTML, TEXT_HTML);
        MAP.put(Extension.CSS, TEXT_CSS);
        MAP.put(Extension.JS, TEXT_JS);

        MAP.put(Extension.MAP, APPLICATION_JSON);

        MAP.put(Extension.PNG, IMAGE_PNG);
        MAP.put(Extension.SVG, IMAGE_SVG);
        MAP.put(Extension.ICO, IMAGE_ICO);

        MAP.put(Extension.EOT, APPLICATION_EOT);
        MAP.put(Extension.TTF, APPLICATION_TTF);
        MAP.put(Extension.WOFF, APPLICATION_WOFF);
        MAP.put(Extension.WOFF2, APPLICATION_WOFF2);
    }

    public static String contentType(String path) {
        int idx = path.lastIndexOf('.');
        String contentType = MAP.get(path.substring(idx + 1));

        if (contentType == null) {
            throw new IllegalArgumentException("Unknown content type: " + path);
        }

        return contentType;
    }
}
