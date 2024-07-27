package com.github.jurisliepins.network.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class TcpConnection implements Closeable {

    private final Socket socket;

    public TcpConnection(final Socket socket) {
        this.socket = socket;
    }

    public TcpConnection(final String host, final int port) throws IOException {
        socket = new Socket(host, port);
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public InetSocketAddress remoteEndpoint() {
        return (InetSocketAddress) socket.getRemoteSocketAddress();
    }

    public InetSocketAddress localEndpoint() {
        return (InetSocketAddress) socket.getLocalSocketAddress();
    }

    public OutputStream outputStream() throws IOException {
        return socket.getOutputStream();
    }

    public InputStream inputStream() throws IOException {
        return socket.getInputStream();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public String toString() {
        try {
            if (isConnected()) {
                return "TcpConnection[%s, %s]".formatted(localEndpoint(), remoteEndpoint());
            }
        } catch (Exception e) {
            // Ignored.
        }
        return "TcpConnection[Disconnected]";
    }
}
