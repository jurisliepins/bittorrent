package com.github.jurisliepins.network;

import java.io.IOException;
import java.net.SocketAddress;

public interface ConnectionFactory {

    Connection connect() throws IOException;

    Connection connect(SocketAddress endpoint) throws IOException;

}
