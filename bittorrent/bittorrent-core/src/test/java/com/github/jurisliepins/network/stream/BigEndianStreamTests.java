package com.github.jurisliepins.network.stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Big-endian stream tests")
public final class BigEndianStreamTests {

    @Test
    @DisplayName("Should write/read byte[]")
    public void shouldWriteReadByteArray() throws IOException {
        final byte[] buffer = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final BigEndianWriter writer = new BigEndianWriter(out);
        writer.write(buffer);
        writer.flush();

        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        final BigEndianReader reader = new BigEndianReader(in);

        assertArrayEquals(buffer, reader.readAllBytes());
    }

    @ParameterizedTest
    @DisplayName("Should write/read byte")
    @ValueSource(bytes = {Byte.MAX_VALUE, Byte.MAX_VALUE})
    public void shouldWriteReadByte(final byte value) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final BigEndianWriter writer = new BigEndianWriter(out);
        writer.writeByte(value);
        writer.flush();

        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        final BigEndianReader reader = new BigEndianReader(in);

        assertEquals(value, reader.readByte());
    }

    @ParameterizedTest
    @DisplayName("Should write/read short")
    @ValueSource(shorts = {Short.MAX_VALUE, Short.MAX_VALUE})
    public void shouldWriteReadShort(final short value) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final BigEndianWriter writer = new BigEndianWriter(out);
        writer.writeShort(value);
        writer.flush();

        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        final BigEndianReader reader = new BigEndianReader(in);

        assertEquals(value, reader.readShort());
    }

    @ParameterizedTest
    @DisplayName("Should write/read integers")
    @ValueSource(ints = {Integer.MAX_VALUE, Integer.MAX_VALUE})
    public void shouldWriteReadIntegers(final int value) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final BigEndianWriter writer = new BigEndianWriter(out);
        writer.writeInt(value);
        writer.flush();

        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        final BigEndianReader reader = new BigEndianReader(in);

        assertEquals(value, reader.readInt());
    }

    @ParameterizedTest
    @DisplayName("Should write/read longs")
    @ValueSource(longs = {Long.MAX_VALUE, Long.MAX_VALUE})
    public void shouldWriteReadLongs(final long value) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final BigEndianWriter writer = new BigEndianWriter(out);
        writer.writeLong(value);
        writer.flush();

        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        final BigEndianReader reader = new BigEndianReader(in);

        assertEquals(value, reader.readLong());
    }
}
