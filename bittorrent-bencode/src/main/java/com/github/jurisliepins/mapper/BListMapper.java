package com.github.jurisliepins.mapper;

import com.github.jurisliepins.value.BValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class BListMapper {

    public static Object read(final BValue value, final Class<?> type, final Class<?> genericType) {
        return switch (type.getName()) {
            case "[B" -> readByteArray(value);
            case "[S" -> readShortArray(value);
            case "[I" -> readIntegerArray(value);
            case "[J" -> readLongArray(value);
            case "[Z" -> readBooleanArray(value);
            case "[C" -> readCharacterArray(value);
            case "[F" -> readFloatArray(value);
            case "[D" -> readDoubleArray(value);
            default -> readDefault(value, genericType);
        };
    }

    private static byte[] readByteArray(final BValue value) {
        final List<BValue> list = value.toList();
        final byte[] array = new byte[list.size()];
        for (int idx = 0; idx < list.size(); idx++) {
            array[idx] = BIntegerMapper.readByte(list.get(idx));
        }
        return array;
    }

    private static short[] readShortArray(final BValue value) {
        final List<BValue> list = value.toList();
        final short[] array = new short[list.size()];
        for (int idx = 0; idx < list.size(); idx++) {
            array[idx] = BIntegerMapper.readShort(list.get(idx));
        }
        return array;
    }

    private static int[] readIntegerArray(final BValue value) {
        final List<BValue> list = value.toList();
        final int[] array = new int[list.size()];
        for (int idx = 0; idx < list.size(); idx++) {
            array[idx] = BIntegerMapper.readInteger(list.get(idx));
        }
        return array;
    }

    private static long[] readLongArray(final BValue value) {
        final List<BValue> list = value.toList();
        final long[] array = new long[list.size()];
        for (int idx = 0; idx < list.size(); idx++) {
            array[idx] = BIntegerMapper.readLong(list.get(idx));
        }
        return array;
    }

    private static boolean[] readBooleanArray(final BValue value) {
        final List<BValue> list = value.toList();
        final boolean[] array = new boolean[list.size()];
        for (int idx = 0; idx < list.size(); idx++) {
            array[idx] = BIntegerMapper.readBoolean(list.get(idx));
        }
        return array;
    }

    private static char[] readCharacterArray(final BValue value) {
        final List<BValue> list = value.toList();
        final char[] array = new char[list.size()];
        for (int idx = 0; idx < list.size(); idx++) {
            array[idx] = BIntegerMapper.readCharacter(list.get(idx));
        }
        return array;
    }

    private static float[] readFloatArray(final BValue value) {
        final List<BValue> list = value.toList();
        final float[] array = new float[list.size()];
        for (int idx = 0; idx < list.size(); idx++) {
            array[idx] = BIntegerMapper.readFloat(list.get(idx));
        }
        return array;
    }

    private static double[] readDoubleArray(final BValue value) {
        final List<BValue> list = value.toList();
        final double[] array = new double[list.size()];
        for (int idx = 0; idx < list.size(); idx++) {
            array[idx] = BIntegerMapper.readDouble(list.get(idx));
        }
        return array;
    }

    private static Object readDefault(final BValue value, final Class<?> genericType) {
        return value.toList()
                .stream()
                .map(val -> BObjectMapper.read(val, genericType, genericType))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
