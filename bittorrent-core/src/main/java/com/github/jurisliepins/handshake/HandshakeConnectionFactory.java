package com.github.jurisliepins.handshake;

import com.github.jurisliepins.network.Connection;

public interface HandshakeConnectionFactory {

    HandshakeConnection create(Connection connection);

}
