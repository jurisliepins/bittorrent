package com.github.jurisliepins.network;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("TCP client tests")
public final class TcpConnectionTests {

    private static final String LISTEN_HOST = "localhost";
    private static final int LISTEN_PORT = 6590;

    private static final int B1 = 65;
    private static final int B2 = 90;

    private TcpConnectionListener listener;

    @BeforeEach
    void beforeEach() throws IOException {
        listener = new TcpConnectionListener(LISTEN_PORT);
    }

    @AfterEach
    void afterEach() throws IOException {
        listener.close();
    }

    @Test
    @DisplayName("Should TCP connection connect and disconnect")
    public void shouldTcpConnectionConnectAndDisconnect() throws IOException {
        try (TcpConnection connection = new TcpConnection(LISTEN_HOST, LISTEN_PORT)) {
            log("Connected %s".formatted(connection));
        }
        log("Disconnected");
    }

    @Test
    @DisplayName("Should TCP connection have access to endpoints")
    public void shouldTcpConnectionHaveAccessToEndpoints() throws IOException {
        try (TcpConnection connection = new TcpConnection(LISTEN_HOST, LISTEN_PORT)) {
            log("Connected %s".formatted(connection));

            final InetSocketAddress localEndpoint = connection.localEndpoint();
            assertNotNull(localEndpoint);
            log("Local endpoint %s".formatted(localEndpoint));

            final InetSocketAddress remoteEndpoint = connection.remoteEndpoint();
            assertNotNull(remoteEndpoint);
            log("Remote endpoint %s".formatted(remoteEndpoint));
        }
        log("Disconnected");
    }

    @Test
    @DisplayName("Should TCP connection read and write")
    public void shouldTcpConnectionReadAndWrite() throws IOException {
        try (TcpConnection connection = new TcpConnection(LISTEN_HOST, LISTEN_PORT)) {
            log("Connected %s".formatted(connection));

            Thread.ofVirtual()
                    .name("listener-thread")
                    .start(() -> {
                        try (TcpConnection acceptedConnection = listener.accept()) {
                            final int b1 = acceptedConnection.inputStream().read();
                            final int b2 = acceptedConnection.inputStream().read();
                            acceptedConnection.outputStream().write(b1);
                            acceptedConnection.outputStream().write(b2);
                            acceptedConnection.outputStream().flush();
                        } catch (IOException e) {
                            throw new NetworkException("Failed to accept connection", e);
                        }
                    });

            connection.outputStream().write(B1);
            connection.outputStream().write(B2);
            connection.outputStream().flush();
            final int b1 = connection.inputStream().read();
            log("Read value %d".formatted(b1));
            assertEquals(B1, b1);
            final int b2 = connection.inputStream().read();
            log("Read value %d".formatted(b2));
            assertEquals(B2, b2);
        }
        log("Disconnected");
    }

    private static void log(final String message) {
        System.out.println(message);
    }
}
