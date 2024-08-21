package com.github.jurisliepins.mapper;

import com.github.jurisliepins.BException;
import com.github.jurisliepins.value.BInteger;

public final class BIntegerMapper {

    @SuppressWarnings("unchecked")
    public static <T> T read(final BInteger value, final Class<T> type) {
        return switch (type.getName()) {
            case "char", "java.lang.Character" -> (T) readCharacter(value);
            case "byte", "java.lang.Byte" -> (T) readByte(value);
            case "short", "java.lang.Short" -> (T) readShort(value);
            case "int", "java.lang.Integer" -> (T) readInteger(value);
            case "long", "java.lang.Long" -> (T) readLong(value);
            case "boolean", "java.lang.Boolean" -> throw new BException("Type '%s' is not supported".formatted(boolean.class.getName()));
            case "float", "java.lang.Float" -> throw new BException("Type '%s' is not supported".formatted(float.class.getName()));
            case "double", "java.lang.Double" -> throw new BException("Type '%s' is not supported".formatted(double.class.getName()));
            default -> (T) readDefault(value);
        };
    }

    public static Character readCharacter(final BInteger value) {
        return value.toCharacter();
    }

    public static Byte readByte(final BInteger value) {
        return value.toByte();
    }

    public static Short readShort(final BInteger value) {
        return value.toShort();
    }

    public static Integer readInteger(final BInteger value) {
        return value.toInteger();
    }

    public static Long readLong(final BInteger value) {
        return value.toLong();
    }

    public static Object readDefault(final BInteger value) {
        return value.value();
    }
}
