package com.github.jurisliepins.network.stream;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;

public final class BigEndianWriter extends DataOutputStream {
    public BigEndianWriter(final OutputStream out) {
        super(new BufferedOutputStream(out));
    }
}
