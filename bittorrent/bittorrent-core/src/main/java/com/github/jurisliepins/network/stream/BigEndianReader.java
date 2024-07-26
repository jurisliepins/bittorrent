package com.github.jurisliepins.network.stream;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

public final class BigEndianReader extends DataInputStream {
    public BigEndianReader(final InputStream in) {
        super(new BufferedInputStream(in));
    }
}
