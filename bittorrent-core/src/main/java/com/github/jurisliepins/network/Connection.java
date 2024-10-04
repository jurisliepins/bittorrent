package com.github.jurisliepins.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public interface Connection extends Closeable {

    int write(ByteBuffer buffer) throws IOException;

    int read(ByteBuffer buffer) throws IOException;

    SocketAddress localEndpoint() throws IOException;

    SocketAddress remoteEndpoint() throws IOException;
}
