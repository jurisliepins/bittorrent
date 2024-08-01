package com.github.jurisliepins.network.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

public final class BigEndianWriter {

    private final WritableByteChannel channel;

    public BigEndianWriter(final OutputStream stream) {
        this.channel = Channels.newChannel(Objects.requireNonNull(stream, "stream is null"));
    }

    public void write(final ByteBuffer value) throws IOException {
        channel.write(Objects.requireNonNull(value, "value is null"));
    }

    public void write(final byte[] value) throws IOException {
        write(ByteBuffer.wrap(value).rewind());
    }

    public void write(final byte value) throws IOException {
        write(ByteBuffer.allocate(StreamConstants.BYTE_BYTE_SIZE).put(value).rewind());
    }

    public void write(final short value) throws IOException {
        write(ByteBuffer.allocate(StreamConstants.SHORT_BYTE_SIZE).putShort(value).rewind());
    }

    public void write(final int value) throws IOException {
        write(ByteBuffer.allocate(StreamConstants.INT_BYTE_SIZE).putInt(value).rewind());
    }

    public void write(final long value) throws IOException {
        write(ByteBuffer.allocate(StreamConstants.LONG_BYTE_SIZE).putLong(value).rewind());
    }
}
