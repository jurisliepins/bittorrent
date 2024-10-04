package com.github.jurisliepins.network;

import lombok.NonNull;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;

public final class TcpConnectionListener implements ConnectionListener {

    private final ServerSocketChannel channel;

    public TcpConnectionListener() throws IOException {
        channel = ServerSocketChannel.open();
    }

    public TcpConnectionListener(@NonNull final SocketAddress endpoint) throws IOException {
        channel = ServerSocketChannel.open();
        channel.bind(endpoint);
    }

    public Connection accept() throws IOException {
        return new TcpConnection(channel.accept());
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
