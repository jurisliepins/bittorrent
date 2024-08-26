package com.github.jurisliepins.stream;

import com.github.jurisliepins.BException;

import java.io.ByteArrayInputStream;

public class BInputStream extends ByteArrayInputStream {
    public BInputStream(final byte[] buf) {
        super(buf);
    }

    public byte readByte() {
        var value = read();
        if (value == -1) {
            throw new BException("Reached the end of the stream");
        }
        return (byte) value;
    }

    public byte peekByte() {
        mark(1);
        var value = readByte();
        reset();
        return value;
    }
}
