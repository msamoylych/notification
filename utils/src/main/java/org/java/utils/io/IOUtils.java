package org.java.utils.io;

import org.java.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by msamoylych on 20.04.2017.
 */
public class IOUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);

    private static final int BUFFER_SIZE = 1024 * 4;

    public static final int EOF = -1;

    public static String toString(InputStream is) throws IOException {
        return toString(is, StringUtils.DEFAULT_CHARSET);
    }

    public static String toString(InputStream is, Charset charset) throws IOException {
        InputStreamReader reader = new InputStreamReader(is, charset);
        StringBuilderWriter writer = new StringBuilderWriter();
        copy(reader, writer);
        return writer.toString();
    }

    public static void copy(Reader r, Writer w) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        int n;
        while (EOF != (n = r.read(buffer))) {
            w.write(buffer, 0, n);
        }
    }

    public static void closeQuietly(Closeable is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException ex) {
            LOGGER.warn(ex.getMessage(), ex);
        }
    }
}
