package com.github.jurisliepins.network;

import java.io.IOException;
import java.net.SocketAddress;

public interface ConnectionFactory {

    Connection create() throws IOException;

    Connection create(SocketAddress endpoint) throws IOException;

}
