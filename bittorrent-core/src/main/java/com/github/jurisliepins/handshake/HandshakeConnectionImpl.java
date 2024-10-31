package com.github.jurisliepins.handshake;

import com.github.jurisliepins.network.Connection;
import lombok.NonNull;

import java.io.IOException;

public final class HandshakeConnectionImpl implements HandshakeConnection {

    private final Connection connection;

    public HandshakeConnectionImpl(@NonNull final Connection connection) {
        this.connection = connection;
    }

    @Override
    public void write(@NonNull final Handshake handshake) throws IOException {
        connection.write((byte) handshake.protocol().length);
        connection.write(handshake.protocol());
        connection.write(handshake.reserved());
        connection.write(handshake.infoHash());
        connection.write(handshake.peerId());
    }

    @Override
    public Handshake read() throws IOException {
        var length = connection.readByte();
        var protocol = connection.readBytes(length);
        var reserved = connection.readBytes(Handshake.RESERVED_LENGTH);
        var infoHash = connection.readBytes(Handshake.INFO_HASH_LENGTH);
        var peerId = connection.readBytes(Handshake.PEER_ID_LENGTH);
        return new Handshake(protocol, reserved, infoHash, peerId);
    }

    @Override
    public Connection connection() {
        return connection;
    }
}
