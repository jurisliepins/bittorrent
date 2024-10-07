package com.github.jurisliepins.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public interface Connection extends Closeable {

    int write(ByteBuffer buffer) throws IOException;

    void write(byte[] value) throws IOException;

    void write(byte value) throws IOException;

    void write(short value) throws IOException;

    void write(int value) throws IOException;

    void write(long value) throws IOException;

    int read(ByteBuffer buffer) throws IOException;

    ByteBuffer readByteBuffer(int count) throws IOException;

    byte[] readBytes(int count) throws IOException;

    byte readByte() throws IOException;

    short readShort() throws IOException;

    int readInt() throws IOException;

    long readLong() throws IOException;

    SocketAddress localEndpoint() throws IOException;

    SocketAddress remoteEndpoint() throws IOException;
}
