package com.github.jurisliepins.network.stream;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

public final class BigEndianWriter implements Closeable {

    private final DataOutputStream stream;
    private final WritableByteChannel channel;

    public BigEndianWriter(final OutputStream stream) {
        Objects.requireNonNull(stream, "stream is null");
        this.stream = new DataOutputStream(new BufferedOutputStream(stream));
        this.channel = Channels.newChannel(stream);
    }

    public void write(final byte[] value) throws IOException {
        stream.write(value);
    }

    public void write(final ByteBuffer value) throws IOException {
        channel.write(Objects.requireNonNull(value, "value is null"));
    }

    public void write(final byte value) throws IOException {
        stream.writeByte(value);
    }

    public void write(final short value) throws IOException {
        stream.writeShort(value);
    }

    public void write(final int value) throws IOException {
        stream.writeInt(value);
    }

    public void write(final long value) throws IOException {
        stream.writeLong(value);
    }

    public void flush() throws IOException {
        stream.flush();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
