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
    @DisplayName("Should write/read succeed")
    public void shouldWriteReadSucceed() throws IOException {
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
    @DisplayName("Should write/read bytes succeed")
    public void shouldWriteReadBytesSucceed() throws IOException {
        try (var connection = new TcpConnection(new InetSocketAddress(PORT))) {
            try (var acceptedConnection = listener.accept()) {
                var message = "Hello, World!".getBytes(StandardCharsets.US_ASCII);
                connection.write(message);
                assertArrayEquals(message, acceptedConnection.readBytes(message.length));
            }
        }
    }

    @Test
    @DisplayName("Should write/read byte succeed")
    public void shouldWriteReadByteSucceed() throws IOException {
        try (var connection = new TcpConnection(new InetSocketAddress(PORT))) {
            try (var acceptedConnection = listener.accept()) {
                var message = (byte) 32;
                connection.write(message);
                assertEquals(message, acceptedConnection.readByte());
            }
        }
    }

    @Test
    @DisplayName("Should write/read short succeed")
    public void shouldWriteReadShortSucceed() throws IOException {
        try (var connection = new TcpConnection(new InetSocketAddress(PORT))) {
            try (var acceptedConnection = listener.accept()) {
                var message = (short) 32;
                connection.write(message);
                assertEquals(message, acceptedConnection.readShort());
            }
        }
    }

    @Test
    @DisplayName("Should write/read int succeed")
    public void shouldWriteReadIntegerSucceed() throws IOException {
        try (var connection = new TcpConnection(new InetSocketAddress(PORT))) {
            try (var acceptedConnection = listener.accept()) {
                var message = 32;
                connection.write(message);
                assertEquals(message, acceptedConnection.readInt());
            }
        }
    }

    @Test
    @DisplayName("Should write/read long succeed")
    public void shouldWriteReadLongSucceed() throws IOException {
        try (var connection = new TcpConnection(new InetSocketAddress(PORT))) {
            try (var acceptedConnection = listener.accept()) {
                var message = 32L;
                connection.write(message);
                assertEquals(message, acceptedConnection.readLong());
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
