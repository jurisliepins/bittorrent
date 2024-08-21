package com.github.jurisliepins.mapper;

import com.github.jurisliepins.BException;
import com.github.jurisliepins.value.BByteString;
import com.github.jurisliepins.value.BValue;

import java.nio.charset.StandardCharsets;

public final class BByteStringMapper {

    @SuppressWarnings("unchecked")
    public static <T> T read(final BByteString value, final Class<T> type) {
        return switch (type.getName()) {
            case "[B" -> (T) readBytes(value);
            case "java.lang.String" -> (T) readString(value);
            default -> (T) readDefault(value);
        };
    }

    public static byte[] readBytes(final BByteString value) {
        return value.toBytes();
    }

    public static String readString(final BByteString value) {
        return value.toString(StandardCharsets.UTF_8);
    }

    public static Object readDefault(final BByteString value) {
        return value.toString(StandardCharsets.UTF_8);
    }

    public static <T> BValue write(final T value) {
        return switch (value.getClass().getName()) {
            case "[B" -> write((byte[]) value);
            case "java.lang.String" -> write((String) value);
            default -> throw new BException("Type '%s' is not supported".formatted(value.getClass().getName()));
        };
    }

    public static BByteString write(final byte[] value) {
        return BByteString.of(value);
    }

    public static BByteString write(final String value) {
        return BByteString.of(value);
    }
}
