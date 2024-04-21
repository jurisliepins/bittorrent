package com.github.jurisliepins;

import com.github.jurisliepins.stream.BOutputStream;
import com.github.jurisliepins.value.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class BEncoder {

    private static final byte[] I_BYTE = new byte[]{ 'i' };
    private static final byte[] S_BYTE = new byte[]{ ':' };
    private static final byte[] L_BYTE = new byte[]{ 'l' };
    private static final byte[] D_BYTE = new byte[]{ 'd' };
    private static final byte[] E_BYTE = new byte[]{ 'e' };

    public static BOutputStream toStream(BValue value) throws IOException {
        var stream = new BOutputStream();
        write(stream, value);
        return stream;
    }

    public static byte[] toBytes(BValue value) throws IOException {
        try (var stream = toStream(value)) {
            return stream.toByteArray();
        }
    }

    public static String toString(BValue value, Charset encoding) throws IOException {
        return new String(toBytes(value), encoding);
    }

    public static void write(BOutputStream stream, BValue value) throws IOException {
        switch (value) {
            case BInteger val -> write(stream, val);
            case BByteString val -> write(stream, val);
            case BList val -> write(stream, val);
            case BDictionary val -> write(stream, val);
        }
    }

    private static void write(BOutputStream stream, BInteger value) throws IOException {
        stream.write(I_BYTE);
        stream.write(((Long) value.value())
                .toString()
                .getBytes(StandardCharsets.US_ASCII));
        stream.write(E_BYTE);
        stream.flush();
    }

    private static void write(BOutputStream stream, BByteString value) throws IOException {
        stream.write(((Integer) value.value().length)
                .toString()
                .getBytes(StandardCharsets.US_ASCII));
        stream.write(S_BYTE);
        stream.write(value.value());
        stream.flush();
    }

    private static void write(BOutputStream stream, BList value) throws IOException {
        stream.write(L_BYTE);
        for (var val : value.value()) {
            write(stream, val);
        }
        stream.write(E_BYTE);
        stream.flush();
    }

    private static void write(BOutputStream stream, BDictionary value) throws IOException {
        stream.write(D_BYTE);
        for (var val : value.value().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList()) {
            write(stream, val.getKey());
            write(stream, val.getValue());
        }
        stream.write(E_BYTE);
        stream.flush();
    }

}
