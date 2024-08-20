package com.github.jurisliepins.mapper;

import com.github.jurisliepins.value.BValue;

import java.nio.charset.StandardCharsets;

public final class BByteStringMapper {

    public static Object read(final BValue value, final Class<?> clazz) {
        return switch (clazz.getName()) {
            case "[B" -> readByteArray(value);
            case "String" -> readString(value);
            default -> readDefault(value);
        };
    }

    private static byte[] readByteArray(final BValue value) {
        return value.toBytes();
    }

    private static String readString(final BValue value) {
        return value.toString(StandardCharsets.UTF_8);
    }

    private static Object readDefault(final BValue value) {
        return value.toString(StandardCharsets.UTF_8);
    }
}
