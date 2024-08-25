package com.github.jurisliepins.mapper;

import com.github.jurisliepins.BException;
import com.github.jurisliepins.value.BInteger;
import com.github.jurisliepins.value.BList;
import com.github.jurisliepins.value.BValue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public final class BListMapper {

    @SuppressWarnings("unchecked")
    public static <T> T read(final BList value, final Class<T> type) {
        if (type.isArray()) {
            return switch (type.getName()) {
                case "[Z" -> (T) readBooleans(value);
                case "[C" -> (T) readCharacters(value);
                case "[B" -> (T) readBytes(value);
                case "[S" -> (T) readShorts(value);
                case "[I" -> (T) readIntegers(value);
                case "[J" -> (T) readLongs(value);
                case "[F" -> (T) readFloats(value);
                case "[D" -> (T) readDoubles(value);
                default ->  (T) readArray(value, type.componentType());
            };
        }
        return (T) readCollection(value);
    }

    public static boolean[] readBooleans(final BList value) {
        final boolean[] array = new boolean[value.value().size()];
        for (int idx = 0; idx < value.value().size(); idx++) {
            array[idx] = BIntegerMapper.readBoolean(value.value().get(idx).toBInteger());
        }
        return array;
    }

    public static char[] readCharacters(final BList value) {
        final char[] array = new char[value.value().size()];
        for (int idx = 0; idx < value.value().size(); idx++) {
            array[idx] = BIntegerMapper.readCharacter(value.value().get(idx).toBInteger());
        }
        return array;
    }

    public static byte[] readBytes(final BList value) {
        final byte[] array = new byte[value.value().size()];
        for (int idx = 0; idx < value.value().size(); idx++) {
            array[idx] = BIntegerMapper.readByte(value.value().get(idx).toBInteger());
        }
        return array;
    }

    public static short[] readShorts(final BList value) {
        final short[] array = new short[value.value().size()];
        for (int idx = 0; idx < value.value().size(); idx++) {
            array[idx] = BIntegerMapper.readShort(value.value().get(idx).toBInteger());
        }
        return array;
    }

    public static int[] readIntegers(final BList value) {
        final int[] array = new int[value.value().size()];
        for (int idx = 0; idx < value.value().size(); idx++) {
            array[idx] = BIntegerMapper.readInteger(value.value().get(idx).toBInteger());
        }
        return array;
    }

    public static long[] readLongs(final BList value) {
        final long[] array = new long[value.value().size()];
        for (int idx = 0; idx < value.value().size(); idx++) {
            array[idx] = BIntegerMapper.readLong(value.value().get(idx).toBInteger());
        }
        return array;
    }

    public static float[] readFloats(final BList value) {
        final float[] array = new float[value.value().size()];
        for (int idx = 0; idx < value.value().size(); idx++) {
            array[idx] = BIntegerMapper.readFloat(value.value().get(idx).toBInteger());
        }
        return array;
    }

    public static double[] readDoubles(final BList value) {
        final double[] array = new double[value.value().size()];
        for (int idx = 0; idx < value.value().size(); idx++) {
            array[idx] = BIntegerMapper.readDouble(value.value().get(idx).toBInteger());
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] readArray(final BList value, final Class<T> type) {
        final T[] array = (T[]) Array.newInstance(type, value.value().size());
        for (int idx = 0; idx < value.value().size(); idx++) {
            array[idx] = BObjectMapper.read(value.value().get(idx), type);
        }
        return array;
    }

    public static Collection<Object> readCollection(final BList value) {
        return value.value()
                .stream()
                .map(val -> BObjectMapper.read(val, Object.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @SuppressWarnings("unchecked")
    public static <T> BValue write(final T value) {
        if (value.getClass().isArray()) {
            return switch (value.getClass().getName()) {
                case "[Z" -> write((boolean[]) value);
                case "[B" -> write((byte[]) value);
                case "[C" -> write((char[]) value);
                case "[S" -> write((short[]) value);
                case "[I" -> write((int[]) value);
                case "[J" -> write((long[]) value);
                case "[F" -> write((float[]) value);
                case "[D" -> write((double[]) value);
                default -> write((Object[]) value);
            };
        }
        return write((Collection<Object>) value);
    }

    public static BList write(final boolean[] value) {
        final BValue[] array = new BValue[value.length];
        for (int idx = 0; idx < value.length; idx++) {
            array[idx] = BInteger.of(value[idx]);
        }
        return BList.of(array);
    }

    public static BList write(final byte[] value) {
        final BValue[] array = new BValue[value.length];
        for (int idx = 0; idx < value.length; idx++) {
            array[idx] = BInteger.of(value[idx]);
        }
        return BList.of(array);
    }

    public static BList write(final char[] value) {
        final BValue[] array = new BValue[value.length];
        for (int idx = 0; idx < value.length; idx++) {
            array[idx] = BInteger.of(value[idx]);
        }
        return BList.of(array);
    }

    public static BList write(final short[] value) {
        final BValue[] array = new BValue[value.length];
        for (int idx = 0; idx < value.length; idx++) {
            array[idx] = BInteger.of(value[idx]);
        }
        return BList.of(array);
    }

    public static BList write(final int[] value) {
        final BValue[] array = new BValue[value.length];
        for (int idx = 0; idx < value.length; idx++) {
            array[idx] = BInteger.of(value[idx]);
        }
        return BList.of(array);
    }

    public static BList write(final long[] value) {
        final BValue[] array = new BValue[value.length];
        for (int idx = 0; idx < value.length; idx++) {
            array[idx] = BInteger.of(value[idx]);
        }
        return BList.of(array);
    }

    public static BList write(final float[] value) {
        final BValue[] array = new BValue[value.length];
        for (int idx = 0; idx < value.length; idx++) {
            array[idx] = BInteger.of(value[idx]);
        }
        return BList.of(array);
    }

    public static BList write(final double[] value) {
        final BValue[] array = new BValue[value.length];
        for (int idx = 0; idx < value.length; idx++) {
            array[idx] = BInteger.of(value[idx]);
        }
        return BList.of(array);
    }

    public static BList write(final Object[] value) {
        final BValue[] array = new BValue[value.length];
        for (int idx = 0; idx < value.length; idx++) {
            array[idx] = BObjectMapper.write(value[idx]);
        }
        return BList.of(array);
    }

    public static BList write(final Collection<Object> value) {
        return BList.of(value.stream()
                .map(BObjectMapper::write)
                .collect(Collectors.toList()));
    }
}
