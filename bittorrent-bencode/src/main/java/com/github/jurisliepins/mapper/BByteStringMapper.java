package com.github.jurisliepins.mapper;

import com.github.jurisliepins.value.BByteString;

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

    private static byte[] readBytes(final BByteString value) {
        return value.toBytes();
    }

    private static String readString(final BByteString value) {
        return value.toString(StandardCharsets.UTF_8);
    }

    private static Object readDefault(final BByteString value) {
        return value.toString(StandardCharsets.UTF_8);
    }
}
