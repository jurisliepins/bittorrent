package com.github.jurisliepins.mapper;

import com.github.jurisliepins.BException;
import com.github.jurisliepins.value.BList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public final class BListMapper {

    @SuppressWarnings("unchecked")
    public static <T> T read(final BList value, final Class<T> type) {
        if (type.isArray()) {
            return switch (type.getName()) {
                case "[C" -> (T) readCharacters(value);
                case "[B" -> (T) readBytes(value);
                case "[S" -> (T) readShorts(value);
                case "[I" -> (T) readIntegers(value);
                case "[J" -> (T) readLongs(value);
                case "[Z" -> throw new BException("Arrays of type '%s' are not supported".formatted(boolean.class.getName()));
                case "[F" -> throw new BException("Arrays of type '%s' are not supported".formatted(float.class.getName()));
                case "[D" -> throw new BException("Arrays of type '%s' are not supported".formatted(double.class.getName()));
                default ->  (T) readArray(value, type.componentType());
            };
        }
        return (T) readCollection(value);
    }

    private static char[] readCharacters(final BList value) {
        final char[] array = new char[value.value().size()];
        for (int idx = 0; idx < value.value().size(); idx++) {
            array[idx] = BIntegerMapper.readCharacter(value.value().get(idx).toBInteger());
        }
        return array;
    }

    private static byte[] readBytes(final BList value) {
        final byte[] array = new byte[value.value().size()];
        for (int idx = 0; idx < value.value().size(); idx++) {
            array[idx] = BIntegerMapper.readByte(value.value().get(idx).toBInteger());
        }
        return array;
    }

    private static short[] readShorts(final BList value) {
        final short[] array = new short[value.value().size()];
        for (int idx = 0; idx < value.value().size(); idx++) {
            array[idx] = BIntegerMapper.readShort(value.value().get(idx).toBInteger());
        }
        return array;
    }

    private static int[] readIntegers(final BList value) {
        final int[] array = new int[value.value().size()];
        for (int idx = 0; idx < value.value().size(); idx++) {
            array[idx] = BIntegerMapper.readInteger(value.value().get(idx).toBInteger());
        }
        return array;
    }

    private static long[] readLongs(final BList value) {
        final long[] array = new long[value.value().size()];
        for (int idx = 0; idx < value.value().size(); idx++) {
            array[idx] = BIntegerMapper.readLong(value.value().get(idx).toBInteger());
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] readArray(final BList value, final Class<T> type) {
        final T[] array = (T[]) Array.newInstance(type, value.value().size());
        for (int idx = 0; idx < value.value().size(); idx++) {
            array[idx] = BObjectMapper.read(value.value().get(idx), type);
        }
        return array;
    }

    private static Collection<Object> readCollection(final BList value) {
        return value.value()
                .stream()
                .map(val -> BObjectMapper.read(val, Object.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
