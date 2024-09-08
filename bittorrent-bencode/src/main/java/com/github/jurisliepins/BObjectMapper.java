package com.github.jurisliepins;

import com.github.jurisliepins.stream.BInputStream;
import com.github.jurisliepins.stream.BOutputStream;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.jurisliepins.BObjectReader.read;
import static com.github.jurisliepins.BObjectWriter.write;

public final class BObjectMapper {
    private BObjectMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T> T fromStream(final BInputStream value, final Class<T> type) throws IOException {
        return read(BDecoder.fromStream(value), type);
    }

    public static <T> T fromBytes(final byte[] value, final Class<T> type) throws IOException {
        try (var stream = new BInputStream(value)) {
            return fromStream(stream, type);
        }
    }

    public static <T> T fromString(final String value, final Charset encoding, final Class<T> type) throws IOException {
        return fromBytes(value.getBytes(encoding), type);
    }

    public static <T> BOutputStream toStream(final T value) throws IOException {
        return BEncoder.toStream(write(value));
    }

    public static <T> byte[] toBytes(final T value) throws IOException {
        try (var stream = toStream(value)) {
            return stream.toByteArray();
        }
    }

    public static <T> String toString(final T value, final Charset encoding) throws IOException {
        return new String(toBytes(value), encoding);
    }
}
