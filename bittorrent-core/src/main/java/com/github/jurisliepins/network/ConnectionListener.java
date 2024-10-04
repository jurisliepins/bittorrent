package com.github.jurisliepins.network;

import java.io.Closeable;
import java.io.IOException;

public interface ConnectionListener extends Closeable {

    Connection accept() throws IOException;

}
