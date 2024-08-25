package com.github.jurisliepins.mapper;

import com.github.jurisliepins.BException;
import com.github.jurisliepins.value.BInteger;
import com.github.jurisliepins.value.BValue;

public final class BIntegerMapper {

    @SuppressWarnings("unchecked")
    public static <T> T read(final BInteger value, final Class<T> type) {
        return switch (type.getName()) {
            case "boolean", "java.lang.Boolean" -> (T) readBoolean(value);
            case "char", "java.lang.Character" -> (T) readCharacter(value);
            case "byte", "java.lang.Byte" -> (T) readByte(value);
            case "short", "java.lang.Short" -> (T) readShort(value);
            case "int", "java.lang.Integer" -> (T) readInteger(value);
            case "long", "java.lang.Long" -> (T) readLong(value);
            case "float", "java.lang.Float" -> (T) readFloat(value);
            case "double", "java.lang.Double" -> (T) readDouble(value);
            default -> throw new BException("Type '%s' is not supported".formatted(type.getName()));
        };
    }

    public static Boolean readBoolean(final BInteger value) {
        return value.toBoolean();
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

    public static Float readFloat(final BInteger value) {
        return value.toFloat();
    }

    public static Double readDouble(final BInteger value) {
        return value.toDouble();
    }

    public static <T> BValue write(final T value) {
        return switch (value.getClass().getName()) {
            case "boolean", "java.lang.Boolean" -> write((boolean) value);
            case "char", "java.lang.Character" -> write((char) value);
            case "byte", "java.lang.Byte" -> write((byte) value);
            case "short", "java.lang.Short" -> write((short) value);
            case "int", "java.lang.Integer" -> write((int) value);
            case "long", "java.lang.Long" -> write((long) value);
            case "float", "java.lang.Float" -> write((float) value);
            case "double", "java.lang.Double" -> write((double) value);
            default -> throw new BException("Type '%s' is not supported".formatted(value.getClass().getName()));
        };
    }

    public static BInteger write(final boolean value) {
        return BInteger.of(value);
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

    public static BInteger write(final float value) {
        return BInteger.of(value);
    }

    public static BInteger write(final double value) {
        return BInteger.of(value);
    }
}
