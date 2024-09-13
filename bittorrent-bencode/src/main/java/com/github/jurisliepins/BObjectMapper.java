package com.github.jurisliepins;

import com.github.jurisliepins.stream.BInputStream;
import com.github.jurisliepins.stream.BOutputStream;
import com.github.jurisliepins.value.BByteString;
import com.github.jurisliepins.value.BDictionary;
import com.github.jurisliepins.value.BInteger;
import com.github.jurisliepins.value.BList;
import com.github.jurisliepins.value.BValue;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public static <T> T read(final BValue value, final Class<T> type) {
        return switch (value) {
            case BByteString val -> readBByteString(val, type);
            case BInteger val -> readBInteger(val, type);
            case BList val -> readBList(val, type);
            case BDictionary val -> readBDictionary(val, type);
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> T readBByteString(final BByteString value, final Class<T> type) {
        return switch (type.getName()) {
            case "[B" -> (T) readBytes(value);
            case "java.lang.String" -> (T) readString(value);
            default -> throw new BException("Type '%s' is not supported".formatted(value.getClass().getName()));
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> T readBInteger(final BInteger value, final Class<T> type) {
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

    @SuppressWarnings("unchecked")
    private static <T> T readBList(final BList value, final Class<T> type) {
        return switch (type.getName()) {
            case "[Z" -> (T) readBooleans(value);
            case "[C" -> (T) readCharacters(value);
            case "[B" -> (T) readBytes(value);
            case "[S" -> (T) readShorts(value);
            case "[I" -> (T) readIntegers(value);
            case "[J" -> (T) readLongs(value);
            case "[F" -> (T) readFloats(value);
            case "[D" -> (T) readDoubles(value);
            case String val when type.isArray() -> (T) readArray(value, type.componentType());
            default -> throw new BException("Type '%s' is not supported".formatted(type.getName()));
        };
    }

    private static <T> T readBDictionary(final BDictionary value, final Class<T> type) {
        var args = Arrays.stream(type.getDeclaredFields())
                .map(field -> {
                    var property = field.getAnnotation(BProperty.class);
                    if (property != null) {
                        var val = value.value().get(BByteString.of(property.value()));
                        if (val != null) {
                            return read(val, field.getType());
                        }
                    }
                    return null;
                })
                .toArray();
        return createInstance(type, args);
    }

    private static byte[] readBytes(final BByteString value) {
        return value.toBytes();
    }

    private static String readString(final BByteString value) {
        return value.toString(StandardCharsets.UTF_8);
    }

    private static Boolean readBoolean(final BInteger value) {
        return value.toBoolean();
    }

    private static Character readCharacter(final BInteger value) {
        return value.toCharacter();
    }

    private static Byte readByte(final BInteger value) {
        return value.toByte();
    }

    private static Short readShort(final BInteger value) {
        return value.toShort();
    }

    private static Integer readInteger(final BInteger value) {
        return value.toInteger();
    }

    private static Long readLong(final BInteger value) {
        return value.toLong();
    }

    private static Float readFloat(final BInteger value) {
        return value.toFloat();
    }

    private static Double readDouble(final BInteger value) {
        return value.toDouble();
    }

    private static boolean[] readBooleans(final BList value) {
        var array = new boolean[value.value().size()];
        for (var idx = 0; idx < value.value().size(); idx++) {
            array[idx] = readBoolean(value.value().get(idx).toBInteger());
        }
        return array;
    }

    private static char[] readCharacters(final BList value) {
        var array = new char[value.value().size()];
        for (var idx = 0; idx < value.value().size(); idx++) {
            array[idx] = readCharacter(value.value().get(idx).toBInteger());
        }
        return array;
    }

    private static byte[] readBytes(final BList value) {
        var array = new byte[value.value().size()];
        for (var idx = 0; idx < value.value().size(); idx++) {
            array[idx] = readByte(value.value().get(idx).toBInteger());
        }
        return array;
    }

    private static short[] readShorts(final BList value) {
        var array = new short[value.value().size()];
        for (var idx = 0; idx < value.value().size(); idx++) {
            array[idx] = readShort(value.value().get(idx).toBInteger());
        }
        return array;
    }

    private static int[] readIntegers(final BList value) {
        var array = new int[value.value().size()];
        for (var idx = 0; idx < value.value().size(); idx++) {
            array[idx] = readInteger(value.value().get(idx).toBInteger());
        }
        return array;
    }

    private static long[] readLongs(final BList value) {
        var array = new long[value.value().size()];
        for (var idx = 0; idx < value.value().size(); idx++) {
            array[idx] = readLong(value.value().get(idx).toBInteger());
        }
        return array;
    }

    private static float[] readFloats(final BList value) {
        var array = new float[value.value().size()];
        for (var idx = 0; idx < value.value().size(); idx++) {
            array[idx] = readFloat(value.value().get(idx).toBInteger());
        }
        return array;
    }

    private static double[] readDoubles(final BList value) {
        var array = new double[value.value().size()];
        for (var idx = 0; idx < value.value().size(); idx++) {
            array[idx] = readDouble(value.value().get(idx).toBInteger());
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] readArray(final BList value, final Class<T> type) {
        var array = (T[]) Array.newInstance(type, value.value().size());
        for (var idx = 0; idx < value.value().size(); idx++) {
            array[idx] = read(value.value().get(idx), type);
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    private static <T> T createInstance(final Class<T> type, final Object[] args) {
        try {
            return (T) Arrays.stream(type.getDeclaredConstructors())
                    .findFirst()
                    .orElseThrow(() -> new BException("Type '%s' should have an all args constructor".formatted(type.getName())))
                    .newInstance(args);
        } catch (Exception e) {
            throw new BException("Failed to create new instance of type '%s'".formatted(type.getName()), e);
        }
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
                                    return new AbstractMap.SimpleEntry<BValue, BValue>(key, val);
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
