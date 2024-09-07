package com.github.jurisliepins;

import com.github.jurisliepins.stream.BInputStream;
import com.github.jurisliepins.stream.BOutputStream;
import lombok.NonNull;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.jurisliepins.BObjectReader.read;
import static com.github.jurisliepins.BObjectWriter.write;

public final class BObjectMapper {
    private BObjectMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T> T fromStream(@NonNull final BInputStream value, @NonNull final Class<T> type) throws IOException {
        return read(BDecoder.fromStream(value), type);
    }

    public static <T> T fromBytes(final byte @NonNull [] value, @NonNull final Class<T> type) throws IOException {
        try (var stream = new BInputStream(value)) {
            return fromStream(stream, type);
        }
    }

    public static <T> T fromString(@NonNull final String value, @NonNull final Charset encoding, @NonNull final Class<T> type) throws IOException {
        return fromBytes(value.getBytes(encoding), type);
    }

    public static <T> BOutputStream toStream(@NonNull final T value) throws IOException {
        return BEncoder.toStream(write(value));
    }

    public static <T> byte[] toBytes(@NonNull final T value) throws IOException {
        try (var stream = toStream(value)) {
            return stream.toByteArray();
        }
    }

    public static <T> String toString(@NonNull final T value, @NonNull final Charset encoding) throws IOException {
        return new String(toBytes(value), encoding);
    }
}
