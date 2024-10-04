package com.github.jurisliepins.network;

import java.io.IOException;
import java.net.SocketAddress;

public interface ConnectionListenerFactory {

    ConnectionListener create() throws IOException;

    ConnectionListener create(SocketAddress endpoint) throws IOException;

}
