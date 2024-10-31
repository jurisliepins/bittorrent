package com.github.jurisliepins.handshake;

import com.github.jurisliepins.network.Connection;

public final class HandshakeConnectionFactoryImpl implements HandshakeConnectionFactory {

    public HandshakeConnection create(final Connection connection) {
        return new HandshakeConnectionImpl(connection);
    }

}
