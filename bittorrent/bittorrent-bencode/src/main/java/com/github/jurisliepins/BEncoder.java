package com.github.jurisliepins;

import com.github.jurisliepins.stream.BOutputStream;
import com.github.jurisliepins.value.BValue;
import com.github.jurisliepins.value.BByteString;
import com.github.jurisliepins.value.BInteger;
import com.github.jurisliepins.value.BList;
import com.github.jurisliepins.value.BDictionary;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class BEncoder {
    private static final byte[] I_BYTE = new byte[]{'i'};
    private static final byte[] S_BYTE = new byte[]{':'};
    private static final byte[] L_BYTE = new byte[]{'l'};
    private static final byte[] D_BYTE = new byte[]{'d'};
    private static final byte[] E_BYTE = new byte[]{'e'};

    private BEncoder() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static BOutputStream toStream(final BValue value) throws IOException {
        final BOutputStream stream = new BOutputStream();
        write(stream, value);
        return stream;
    }

    public static byte[] toBytes(final BValue value) throws IOException {
        try (BOutputStream stream = toStream(value)) {
            return stream.toByteArray();
        }
    }

    public static String toString(final BValue value, final Charset encoding) throws IOException {
        return new String(toBytes(value), encoding);
    }

    public static void write(final BOutputStream stream, final BValue value) throws IOException {
        switch (value) {
            case BInteger val -> write(stream, val);
            case BByteString val -> write(stream, val);
            case BList val -> write(stream, val);
            case BDictionary val -> write(stream, val);
        }
    }

    private static void write(final BOutputStream stream, final BInteger value) throws IOException {
        stream.write(I_BYTE);
        stream.write(((Long) value.value())
                .toString()
                .getBytes(StandardCharsets.US_ASCII));
        stream.write(E_BYTE);
        stream.flush();
    }

    private static void write(final BOutputStream stream, final BByteString value) throws IOException {
        stream.write(((Integer) value.value().length)
                .toString()
                .getBytes(StandardCharsets.US_ASCII));
        stream.write(S_BYTE);
        stream.write(value.value());
        stream.flush();
    }

    private static void write(final BOutputStream stream, final BList value) throws IOException {
        stream.write(L_BYTE);
        for (final BValue val : value.value()) {
            write(stream, val);
        }
        stream.write(E_BYTE);
        stream.flush();
    }

    private static void write(final BOutputStream stream, final BDictionary value) throws IOException {
        stream.write(D_BYTE);
        for (final Map.Entry<BValue, BValue> val : value.value().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList()) {
            write(stream, val.getKey());
            write(stream, val.getValue());
        }
        stream.write(E_BYTE);
        stream.flush();
    }

}
