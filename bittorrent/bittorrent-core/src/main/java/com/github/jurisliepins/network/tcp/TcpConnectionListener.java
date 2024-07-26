package com.github.jurisliepins.network.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public final class TcpConnectionListener implements Closeable {

    private final ServerSocket serverSocket;

    public TcpConnectionListener(final ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public TcpConnectionListener(final int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public boolean isBound() {
        return serverSocket.isBound();
    }

    public InetSocketAddress localEndpoint() {
        return (InetSocketAddress) serverSocket.getLocalSocketAddress();
    }

    public TcpConnection accept() throws IOException {
        return new TcpConnection(serverSocket.accept());
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }

    @Override
    public String toString() {
        try {
            if (isBound()) {
                return "TcpConnectionListener(%s)".formatted(localEndpoint());
            }
        } catch (Exception e) {
            // Ignored.
        }
        return "TcpConnectionListener(Unbound)";
    }
}
