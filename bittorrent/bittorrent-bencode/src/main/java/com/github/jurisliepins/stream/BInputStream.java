package com.github.jurisliepins.stream;

import com.github.jurisliepins.BException;

import java.io.ByteArrayInputStream;

public class BInputStream extends ByteArrayInputStream {
    public BInputStream(byte[] buf) {
        super(buf);
    }

    public byte readByte() {
        int value = read();
        if (value == -1) {
            throw new BException("Reached the end of the stream.");
        }
        return (byte) value;
    }

    public byte peekByte() {
        mark(1);
        byte value = readByte();
        reset();
        return value;
    }
}
