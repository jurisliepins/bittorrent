package com.github.jurisliepins.network;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("TCP connection tests")
public final class TcpConnectionTests {

    private static final int PORT = 6881;

    private ConnectionListener listener;

    @BeforeEach
    void setUp() throws IOException {
        listener = new TcpConnectionListener(new InetSocketAddress(PORT));
    }

    @AfterEach
    void tearDown() throws IOException {
        listener.close();
    }

    @Test
    @DisplayName("Should TCP connection open/close succeed")
    public void shouldTcpConnectionSucceed() throws IOException {
        try (var connection = new TcpConnection(new InetSocketAddress(PORT))) {
            try (var acceptedConnection = listener.accept()) {
                assertNotNull(connection);
                assertNotNull(acceptedConnection);
            }
        }
    }

    @Test
    @DisplayName("Should write and read succeed")
    public void shouldWriteAndReadSucceed() throws IOException {
        try (var connection = new TcpConnection(new InetSocketAddress(PORT))) {
            try (var acceptedConnection = listener.accept()) {
                var message = "Hello, World!".getBytes(StandardCharsets.US_ASCII);
                var writeBuffer = ByteBuffer.wrap(message);
                var readBuffer = ByteBuffer.allocate(message.length);
                var w = connection.write(writeBuffer);
                var r = acceptedConnection.read(readBuffer);
                assertEquals(message.length, w);
                assertEquals(message.length, r);
                assertArrayEquals(message, writeBuffer.array());
                assertArrayEquals(message, readBuffer.array());
            }
        }
    }

    @Test
    @DisplayName("Should get addresses succeed")
    public void shouldGetAddressesSucceed() throws IOException {
        try (var connection = new TcpConnection(new InetSocketAddress(PORT))) {
            try (var acceptedConnection = listener.accept()) {
                assertNotNull(connection.localEndpoint());
                assertNotNull(connection.remoteEndpoint());
                assertNotNull(acceptedConnection.localEndpoint());
                assertNotNull(acceptedConnection.remoteEndpoint());
            }
        }
    }

}
