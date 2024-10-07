package com.github.jurisliepins.network;

import lombok.NonNull;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public final class TcpConnection implements Connection {

    private final SocketChannel channel;

    public TcpConnection(@NonNull final SocketChannel channel) {
        this.channel = channel;
    }

    public TcpConnection() throws IOException {
        channel = SocketChannel.open();
    }

    public TcpConnection(@NonNull final SocketAddress endpoint) throws IOException {
        channel = SocketChannel.open(endpoint);
    }

    @Override
    public int write(@NonNull final ByteBuffer buffer) throws IOException {
        return channel.write(buffer.rewind());
    }

    @Override
    public int write(final ByteBuffer buffer) throws IOException {
        return channel.write(buffer);
    }

    @Override
    public void write(final byte @NonNull [] value) throws IOException {
        write(ByteBuffer.wrap(value));
    }

    @Override
    public void write(final byte value) throws IOException {
        write(ByteBuffer.allocate(Byte.BYTES).put(value));
    }

    @Override
    public void write(final short value) throws IOException {
        write(ByteBuffer.allocate(Short.BYTES).putShort(value));
    }

    @Override
    public void write(final int value) throws IOException {
        write(ByteBuffer.allocate(Integer.BYTES).putInt(value));
    }

    @Override
    public int read(final ByteBuffer buffer) throws IOException {
        return channel.read(buffer);
    }

    @Override
    public SocketAddress localEndpoint() throws IOException {
        return channel.getLocalAddress();
    }

    @Override
    public SocketAddress remoteEndpoint() throws IOException {
        return channel.getRemoteAddress();
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
