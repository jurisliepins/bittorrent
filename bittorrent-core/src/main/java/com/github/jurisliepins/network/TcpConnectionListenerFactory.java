package com.github.jurisliepins.network;

import java.io.IOException;
import java.net.SocketAddress;

public final class TcpConnectionListenerFactory implements ConnectionListenerFactory {

    @Override
    public ConnectionListener create() throws IOException {
        return new TcpConnectionListener();
    }

    @Override
    public ConnectionListener create(final SocketAddress endpoint) throws IOException {
        return new TcpConnectionListener(endpoint);
    }
}
