package com.github.jurisliepins.network.tcp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

@DisplayName("TCP connection listener tests")
public class TcpConnectionListenerTests {

    private static final String LISTEN_HOST = "localhost";
    private static final int LISTEN_PORT = 6591;

    @Order(1)
    @Test
    @DisplayName("Should TCP connection listener start and stop")
    public void shouldTcpConnectionListenerStartAndStop() throws IOException {
        try (TcpConnectionListener listener = new TcpConnectionListener(LISTEN_PORT)) {
            logOut("Started listening %s".formatted(listener));
        }
        logOut("Stopped listening");
    }

    @Order(2)
    @Test
    @DisplayName("Should TCP connection listener have access to endpoints")
    public void shouldTcpConnectionListenerHaveAccessToEndpoints() throws IOException {
        try (TcpConnectionListener listener = new TcpConnectionListener(LISTEN_PORT)) {
            logOut("Started listening %s".formatted(listener));

            final InetSocketAddress localEndpoint = listener.localEndpoint();
            logOut("Local endpoint %s".formatted(localEndpoint));
        }
        logOut("Stopped listening");
    }

    @Order(3)
    @Test
    @DisplayName("Should TCP connection listener accept connection")
    public void shouldTcpConnectionListenerAcceptConnection() throws IOException {
        try (TcpConnectionListener listener = new TcpConnectionListener(LISTEN_PORT)) {
            logOut("Started listening %s".formatted(listener));

            try (TcpConnection connection = new TcpConnection(LISTEN_HOST, LISTEN_PORT)) {
                logOut("Connected %s".formatted(connection));

                try (TcpConnection acceptedConnection = listener.accept()) {
                    logOut("Accepted connection %s".formatted(acceptedConnection));
                }
                logOut("Disconnected");
            }
            logOut("Disconnected");
        }
        logOut("Stopped listening");
    }

    private static void logOut(final String message) {
        System.out.println(message);
    }
}
