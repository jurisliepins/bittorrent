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
    public int write(final ByteBuffer buffer) throws IOException {
        return channel.write(buffer);
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
