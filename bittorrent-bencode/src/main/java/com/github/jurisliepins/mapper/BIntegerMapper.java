package com.github.jurisliepins.mapper;

import com.github.jurisliepins.BException;
import com.github.jurisliepins.value.BValue;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class BIntegerMapper {

    public static Object read(final BValue value, final Class<?> clazz) {
        return switch (clazz.getName()) {
            case "byte", "java.lang.Byte" -> readByte(value);
            case "short", "java.lang.Short" -> readShort(value);
            case "int", "java.lang.Integer" -> readInteger(value);
            case "long", "java.lang.Long" -> readLong(value);
            case "float", "java.lang.Float" -> readFloat(value);
            case "double", "java.lang.Double" -> readDouble(value);
            case "boolean", "java.lang.Boolean" -> readBoolean(value);
            case "char", "java.lang.Character" -> readCharacter(value);
            case "java.time.OffsetDateTime" -> readOffsetDateTime(value);
            default -> readDefault(value);
        };
    }

    public static byte readByte(final BValue value) {
        return value.toByte();
    }

    public static short readShort(final BValue value) {
        return value.toShort();
    }

    public static int readInteger(final BValue value) {
        return value.toInteger();
    }

    public static long readLong(final BValue value) {
        return value.toLong();
    }

    public static float readFloat(final BValue value) {
        return (float) (int) value.toInteger();
    }

    public static double readDouble(final BValue value) {
        return (double) (long) value.toLong();
    }

    public static boolean readBoolean(final BValue value) {
        return switch (value.toInteger()) {
            case 1 -> true;
            case 0 -> false;
            default -> throw new BException("Cannot read value %d to boolean".formatted(value.toInteger()));
        };
    }

    public static char readCharacter(final BValue value) {
        return (char) (byte) value.toByte();
    }

    public static OffsetDateTime readOffsetDateTime(final BValue value) {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(value.toLong()), ZoneOffset.UTC);
    }

    public static Object readDefault(final BValue value) {
        return value.toLong();
    }
}
