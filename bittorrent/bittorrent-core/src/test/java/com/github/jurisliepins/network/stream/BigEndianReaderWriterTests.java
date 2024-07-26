package com.github.jurisliepins.network.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class BigEndianReaderWriterTests {

    public void a() {
        final BigEndianWriter w = new BigEndianWriter(new ByteArrayOutputStream());
        final BigEndianReader r = new BigEndianReader(new ByteArrayInputStream(new byte[0]));
    }

}
