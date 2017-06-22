package org.java.utils.io;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by msamoylych on 29.05.2017.
 */
public class StringBuilderWriter extends Writer {

    private final StringBuilder builder;

    public StringBuilderWriter() {
        this.builder = new StringBuilder();
    }

    public StringBuilderWriter(int capacity) {
        this.builder = new StringBuilder(capacity);
    }

    public StringBuilderWriter(final StringBuilder builder) {
        this.builder = builder != null ? builder : new StringBuilder();
    }

    @Override
    public Writer append(char value) {
        builder.append(value);
        return this;
    }

    @Override
    public Writer append(CharSequence value) {
        builder.append(value);
        return this;
    }

    @Override
    public Writer append(CharSequence value, int start, int end) {
        builder.append(value, start, end);
        return this;
    }

    @Override
    public void write(String value) {
        if (value != null) {
            builder.append(value);
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (cbuf != null) {
            builder.append(cbuf, off, len);
        }
    }

    public StringBuilder getBuilder() {
        return builder;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    @Override
    public void flush() throws IOException {
        // no-op
    }

    @Override
    public void close() throws IOException {
        // no-op
    }
}
