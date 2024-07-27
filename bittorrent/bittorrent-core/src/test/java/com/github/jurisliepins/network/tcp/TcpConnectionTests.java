package com.github.jurisliepins.network.tcp;

import com.github.jurisliepins.network.NetworkException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TCP connection tests")
public final class TcpConnectionTests {

    private static final String LISTEN_HOST = "localhost";
    private static final int LISTEN_PORT = 6590;

    private static final int B1 = 65;
    private static final int B2 = 90;

    @Order(1)
    @Test
    @DisplayName("Should TCP connection connect and disconnect")
    public void shouldTcpConnectionConnectAndDisconnect() throws IOException {
        try (TcpConnectionListener listener = new TcpConnectionListener(LISTEN_PORT)) {
            logOut("Started listening %s".formatted(listener));

            try (TcpConnection connection = new TcpConnection(LISTEN_HOST, LISTEN_PORT)) {
                logOut("Connected %s".formatted(connection));
            }
            logOut("Disconnected");
        }
        logOut("Stopped listening");
    }

    @Order(2)
    @Test
    @DisplayName("Should TCP connection have access to endpoints")
    public void shouldTcpConnectionHaveAccessToEndpoints() throws IOException {
        try (TcpConnectionListener listener = new TcpConnectionListener(LISTEN_PORT)) {
            logOut("Started listening %s".formatted(listener));

            try (TcpConnection connection = new TcpConnection(LISTEN_HOST, LISTEN_PORT)) {
                logOut("Connected %s".formatted(connection));

                final InetSocketAddress localEndpoint = connection.localEndpoint();
                logOut("Local endpoint %s".formatted(localEndpoint));

                final InetSocketAddress remoteEndpoint = connection.remoteEndpoint();
                logOut("Remote endpoint %s".formatted(remoteEndpoint));
            }
            logOut("Disconnected");
        }
        logOut("Stopped listening");
    }

    @Order(3)
    @Test
    @DisplayName("Should TCP connection read and write")
    public void shouldTcpConnectionReadAndWrite() throws IOException {
        try (TcpConnectionListener listener = new TcpConnectionListener(LISTEN_PORT)) {
            logOut("Started listening %s".formatted(listener));

            Thread.ofVirtual()
                    .start(() -> {
                        try (TcpConnection acceptedConnection = listener.accept()) {
                            logOut("Accepted connection %s".formatted(acceptedConnection));
                            final int b1 = acceptedConnection.inputStream().read();
                            final int b2 = acceptedConnection.inputStream().read();
                            acceptedConnection.outputStream().write(b1);
                            acceptedConnection.outputStream().write(b2);
                            acceptedConnection.outputStream().flush();
                        } catch (Exception e) {
                            logError("Failed to accept connection %s".formatted(e.toString()));
                            throw new NetworkException("Failed to accept connection", e);
                        }
                        logOut("Disconnected");
                    });

            try (TcpConnection connection = new TcpConnection(LISTEN_HOST, LISTEN_PORT)) {
                logOut("Connected %s".formatted(connection));
                connection.outputStream().write(B1);
                connection.outputStream().write(B2);
                connection.outputStream().flush();

                final int b1 = connection.inputStream().read();
                logOut("Read value %d".formatted(b1));
                assertEquals(B1, b1);

                final int b2 = connection.inputStream().read();
                logOut("Read value %d".formatted(b2));
                assertEquals(B2, b2);
            }
            logOut("Disconnected");
        }
        logOut("Stopped listening");
    }

    private static void logOut(final String message) {
        System.out.println(message);
    }

    private static void logError(final String message) {
        System.err.println(message);
    }
}
