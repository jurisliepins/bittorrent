package com.github.jurisliepins;

import com.github.jurisliepins.value.BByteString;
import com.github.jurisliepins.value.BDictionary;
import com.github.jurisliepins.value.BInteger;
import com.github.jurisliepins.value.BList;
import com.github.jurisliepins.value.BValue;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class BObjectWriter {
    private BObjectWriter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T> BValue write(final T value) {
        return switch (value.getClass().getName()) {
            case "[B" -> writeBytes((byte[]) value);
            case "java.lang.String" -> writeString((String) value);

            case "boolean", "java.lang.Boolean" -> writeBoolean((boolean) value);
            case "char", "java.lang.Character" -> writeCharacter((char) value);
            case "byte", "java.lang.Byte" -> writeByte((byte) value);
            case "short", "java.lang.Short" -> writeShort((short) value);
            case "int", "java.lang.Integer" -> writeInteger((int) value);
            case "long", "java.lang.Long" -> writeLong((long) value);
            case "float", "java.lang.Float" -> writeFloat((float) value);
            case "double", "java.lang.Double" -> writeDouble((double) value);

            case "[Z" -> writeBooleans((boolean[]) value);
            case "[C" -> writeCharacters((char[]) value);
            case "[S" -> writeShorts((short[]) value);
            case "[I" -> writeIntegers((int[]) value);
            case "[J" -> writeLongs((long[]) value);
            case "[F" -> writeFloats((float[]) value);
            case "[D" -> writeDoubles((double[]) value);
            case String val when value.getClass().isArray() -> writeObjects((Object[]) value);

            default -> writeObject(value);
        };
    }

    public static BByteString writeBytes(final byte[] value) {
        return BByteString.of(value);
    }

    public static BByteString writeString(final String value) {
        return BByteString.of(value);
    }

    public static BInteger writeBoolean(final boolean value) {
        return BInteger.of(value);
    }

    public static BInteger writeCharacter(final char value) {
        return BInteger.of(value);
    }

    public static BInteger writeByte(final byte value) {
        return BInteger.of(value);
    }

    public static BInteger writeShort(final short value) {
        return BInteger.of(value);
    }

    public static BInteger writeInteger(final int value) {
        return BInteger.of(value);
    }

    public static BInteger writeLong(final long value) {
        return BInteger.of(value);
    }

    public static BInteger writeFloat(final float value) {
        return BInteger.of(value);
    }

    public static BInteger writeDouble(final double value) {
        return BInteger.of(value);
    }

    public static BList writeBooleans(final boolean[] value) {
        var array = new BValue[value.length];
        for (var idx = 0; idx < value.length; idx++) {
            array[idx] = BInteger.of(value[idx]);
        }
        return BList.of(array);
    }

    public static BList writeCharacters(final char[] value) {
        var array = new BValue[value.length];
        for (var idx = 0; idx < value.length; idx++) {
            array[idx] = BInteger.of(value[idx]);
        }
        return BList.of(array);
    }

    public static BList writeShorts(final short[] value) {
        var array = new BValue[value.length];
        for (var idx = 0; idx < value.length; idx++) {
            array[idx] = BInteger.of(value[idx]);
        }
        return BList.of(array);
    }

    public static BList writeIntegers(final int[] value) {
        var array = new BValue[value.length];
        for (var idx = 0; idx < value.length; idx++) {
            array[idx] = BInteger.of(value[idx]);
        }
        return BList.of(array);
    }

    public static BList writeLongs(final long[] value) {
        var array = new BValue[value.length];
        for (var idx = 0; idx < value.length; idx++) {
            array[idx] = BInteger.of(value[idx]);
        }
        return BList.of(array);
    }

    public static BList writeFloats(final float[] value) {
        var array = new BValue[value.length];
        for (var idx = 0; idx < value.length; idx++) {
            array[idx] = BInteger.of(value[idx]);
        }
        return BList.of(array);
    }

    public static BList writeDoubles(final double[] value) {
        var array = new BValue[value.length];
        for (var idx = 0; idx < value.length; idx++) {
            array[idx] = BInteger.of(value[idx]);
        }
        return BList.of(array);
    }

    public static <T> BList writeObjects(final T[] value) {
        var array = new BValue[value.length];
        for (var idx = 0; idx < value.length; idx++) {
            array[idx] = write(value[idx]);
        }
        return BList.of(array);
    }

    public static <T> BDictionary writeObject(final T value) {
        var values = Arrays.stream(value.getClass().getDeclaredFields())
                .map(field -> {
                    var property = field.getAnnotation(BProperty.class);
                    if (property != null) {
                        try {
                            if (field.trySetAccessible()) {
                                var fieldValue = field.get(value);
                                if (fieldValue != null) {
                                    var key = BByteString.of(property.value());
                                    var val = write(fieldValue);
                                    return new SimpleEntry<BValue, BValue>(key, val);
                                }
                            } else {
                                throw new BException("Failed to set field '%s' as accessible".formatted(field.getName()));
                            }
                        } catch (IllegalAccessException e) {
                            throw new BException("Failed to access field '%s'".formatted(field.getName()), e);
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return BDictionary.of(values);
    }
}
