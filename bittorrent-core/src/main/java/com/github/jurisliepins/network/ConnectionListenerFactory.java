package com.github.jurisliepins.network;

import java.io.IOException;
import java.net.SocketAddress;

public interface ConnectionListenerFactory {

    ConnectionListener listen() throws IOException;

    ConnectionListener listen(SocketAddress endpoint) throws IOException;

}
