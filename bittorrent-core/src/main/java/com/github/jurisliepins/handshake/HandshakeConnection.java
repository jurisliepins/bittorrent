package com.github.jurisliepins.handshake;

import com.github.jurisliepins.network.Connection;

import java.io.IOException;

public interface HandshakeConnection {

    void write(Handshake handshake) throws IOException;

    Handshake read() throws IOException;

    Connection connection();

}
