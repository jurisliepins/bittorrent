package com.github.jurisliepins.mapper;

import com.github.jurisliepins.BException;
import com.github.jurisliepins.value.BInteger;
import com.github.jurisliepins.value.BValue;

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

    public static <T> BValue write(final T value) {
        return switch (value.getClass().getName()) {
            case "char", "java.lang.Character" -> write((char) value);
            case "byte", "java.lang.Byte" -> write((byte) value);
            case "short", "java.lang.Short" -> write((short) value);
            case "int", "java.lang.Integer" -> write((int) value);
            case "long", "java.lang.Long" -> write((long) value);
            default -> throw new BException("Type '%s' is not supported".formatted(value.getClass().getName()));
        };
    }

    public static BInteger write(final char value) {
        return BInteger.of(value);
    }

    public static BInteger write(final byte value) {
        return BInteger.of(value);
    }

    public static BInteger write(final short value) {
        return BInteger.of(value);
    }

    public static BInteger write(final int value) {
        return BInteger.of(value);
    }

    public static BInteger write(final long value) {
        return BInteger.of(value);
    }
}
