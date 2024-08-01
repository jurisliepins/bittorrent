package com.github.jurisliepins.network.stream;

import com.github.jurisliepins.network.NetworkException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;

public final class BigEndianReader {

    private final ReadableByteChannel channel;

    public BigEndianReader(final InputStream stream) {
        this.channel = Channels.newChannel(Objects.requireNonNull(stream, "stream is null"));
    }

    public ByteBuffer readByteBuffer(final int count) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(count);
        if (channel.read(buffer) < 0) {
            throw new NetworkException("Reached end of stream. Connection closed at the other end.");
        }
        return buffer.rewind();
    }

    public byte[] readBytes(final int count) throws IOException {
        return readByteBuffer(count).array();
    }

    public byte readByte() throws IOException {
        return readByteBuffer(StreamConstants.BYTE_BYTE_SIZE).get();
    }

    public short readShort() throws IOException {
        return readByteBuffer(StreamConstants.SHORT_BYTE_SIZE).getShort();
    }

    public int readInt() throws IOException {
        return readByteBuffer(StreamConstants.INT_BYTE_SIZE).getInt();
    }

    public long readLong() throws IOException {
        return readByteBuffer(StreamConstants.LONG_BYTE_SIZE).getLong();
    }
}
