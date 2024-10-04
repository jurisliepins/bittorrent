package com.github.jurisliepins.network;

import lombok.NonNull;

import java.io.IOException;
import java.net.SocketAddress;

public final class TcpConnectionFactory implements ConnectionFactory {
    @Override
    public Connection create() throws IOException {
        return new TcpConnection();
    }

    @Override
    public Connection create(@NonNull final SocketAddress endpoint) throws IOException {
        return new TcpConnection(endpoint);
    }
}
