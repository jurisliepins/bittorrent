package com.github.jurisliepins.network.stream;

import com.github.jurisliepins.network.NetworkException;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;

public final class BigEndianReader implements Closeable {

    private final DataInputStream stream;
    private final ReadableByteChannel channel;

    public BigEndianReader(final InputStream stream) {
        Objects.requireNonNull(stream, "stream is null");
        this.stream = new DataInputStream(new BufferedInputStream(stream));
        this.channel = Channels.newChannel(stream);
    }

    public byte[] readBytes(final int count) throws IOException {
        int offset = 0;
        int length = count;
        final byte[] buffer = new byte[count];
        while (length > 0) {
            final int n = stream.read(buffer, offset, length);
            if (n < 0) {
                throw new NetworkException("Reached end of stream. Connection closed at the other end.");
            }
            offset += n;
            length -= n;
        }
        return buffer;
    }

    public ByteBuffer readByteBuffer(final int count) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(count);
        if (channel.read(buffer) < 0) {
            throw new NetworkException("Reached end of stream. Connection closed at the other end.");
        }
        return buffer;
    }

    public byte readByte() throws IOException {
        return stream.readByte();
    }

    public short readShort() throws IOException {
        return stream.readShort();
    }

    public int readInt() throws IOException {
        return stream.readInt();
    }

    public long readLong() throws IOException {
        return stream.readLong();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
