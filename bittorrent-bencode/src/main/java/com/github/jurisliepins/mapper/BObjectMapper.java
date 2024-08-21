package com.github.jurisliepins.mapper;

import com.github.jurisliepins.BDecoder;
import com.github.jurisliepins.BEncoder;
import com.github.jurisliepins.BException;
import com.github.jurisliepins.BProperty;
import com.github.jurisliepins.stream.BInputStream;
import com.github.jurisliepins.stream.BOutputStream;
import com.github.jurisliepins.value.BValue;
import com.github.jurisliepins.value.BByteString;
import com.github.jurisliepins.value.BInteger;
import com.github.jurisliepins.value.BList;
import com.github.jurisliepins.value.BDictionary;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class BObjectMapper {

    public static <T> T read(final BValue value, final Class<T> type) {
        return switch (value) {
            case BByteString val -> BByteStringMapper.read(val, type);
            case BInteger val -> BIntegerMapper.read(val, type);
            case BList val -> BListMapper.read(val, type);
            case BDictionary val -> BDictionaryMapper.read(val, type);
        };
    }

    public static <T> T fromStream(final BInputStream value, final Class<T> type) throws IOException {
        return read(BDecoder.fromStream(value), type);
    }

    public static <T> T fromBytes(final byte[] value, final Class<T> type) throws IOException {
        try (BInputStream stream = new BInputStream(value)) {
            return fromStream(stream, type);
        }
    }

    public static <T> T fromString(final String value, final Charset encoding, final Class<T> type) throws IOException {
        return fromBytes(value.getBytes(encoding), type);
    }

    // ---

    public <T> BOutputStream writeToStream(final T value) throws IOException {
        final BValue dictionary = writeToBDictionary(value);
        return BEncoder.toStream(dictionary);
    }

    public <T> byte[] writeToBytes(final T value) throws IOException {
        try (BOutputStream stream = writeToStream(value)) {
            return stream.toByteArray();
        }
    }

    public <T> String writeToString(final T value, final Charset encoding) throws IOException {
        return new String(writeToBytes(value), encoding);
    }

    public <T> BDictionary writeToBDictionary(final T value) {
        return writeBDictionary(value);
    }










    private static BValue write(final Object value) {
        return switch (value.getClass().getName()) {
            case "[B" -> writeBByteString((byte[]) value);
            case "java.lang.String" -> writeBByteString((String) value);
            case "byte", "java.lang.Byte" -> writeBInteger((byte) value);
            case "short", "java.lang.Short" -> writeBInteger((short) value);
            case "int", "java.lang.Integer" -> writeBInteger((int) value);
            case "long", "java.lang.Long" -> writeBInteger((long) value);
            case "float", "java.lang.Float" -> writeBInteger((float) value);
            case "double", "java.lang.Double" -> writeBInteger((double) value);
            case "boolean", "java.lang.Boolean" -> writeBInteger((boolean) value);
            case "char", "java.lang.Character" -> writeBInteger((char) value);
            case "java.time.OffsetDateTime" -> writeBInteger((OffsetDateTime) value);
            case "[S" -> writeBList((short[]) value);
            case "[I" -> writeBList((int[]) value);
            case "[J" -> writeBList((long[]) value);
            case "[F" -> writeBList((float[]) value);
            case "[D" -> writeBList((double[]) value);
            case "[Z" -> writeBList((boolean[]) value);
            case "[C" -> writeBList((char[]) value);
            case "java.util.Collection",
                 "java.util.List",
                 "java.util.ArrayList" -> writeBList((Collection<?>) value);
            default -> writeBDictionary(value);
        };
    }

    private static BByteString writeBByteString(final byte[] value) {
        return BByteString.of(value);
    }

    private static BByteString writeBByteString(final String value) {
        return BByteString.of(value);
    }

    private static BInteger writeBInteger(final byte value) {
        return BInteger.of(value);
    }

    private static BInteger writeBInteger(final short value) {
        return BInteger.of(value);
    }

    private static BInteger writeBInteger(final int value) {
        return BInteger.of(value);
    }

    private static BInteger writeBInteger(final long value) {
        return BInteger.of(value);
    }

    private static BInteger writeBInteger(final float value) {
        return BInteger.of(((Float) value).longValue());
    }

    private static BInteger writeBInteger(final double value) {
        return BInteger.of(((Double) value).longValue());
    }

    private static BInteger writeBInteger(final boolean value) {
        return BInteger.of(value ? 1 : 0);
    }

    private static BInteger writeBInteger(final char value) {
        return BInteger.of((byte) value);
    }

    private static BInteger writeBInteger(final OffsetDateTime value) {
        return BInteger.of(value.toEpochSecond());
    }

    private static BList writeBList(final Collection<?> value) {
        return BList.of(value.stream()
                                .map(BObjectMapper::write)
                                .collect(Collectors.toCollection(ArrayList::new)));
    }

    private static BList writeBList(final short[] value) {
        final List<BValue> list = new ArrayList<>(value.length);
        for (final short val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList writeBList(final int[] value) {
        final List<BValue> list = new ArrayList<>(value.length);
        for (final int val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList writeBList(final long[] value) {
        final List<BValue> list = new ArrayList<>(value.length);
        for (final long val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList writeBList(final float[] value) {
        final List<BValue> list = new ArrayList<>(value.length);
        for (final float val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList writeBList(final double[] value) {
        final List<BValue> list = new ArrayList<>(value.length);
        for (final double val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList writeBList(final boolean[] value) {
        final List<BValue> list = new ArrayList<>(value.length);
        for (final boolean val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList writeBList(final char[] value) {
        final List<BValue> list = new ArrayList<>(value.length);
        for (final char val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static <T> BDictionary writeBDictionary(final T value) {
        final BDictionary dictionary = BDictionary.of();
        try {
            Arrays.stream(value.getClass().getDeclaredFields())
                    .forEach(field -> {
                        final BProperty property = field.getAnnotation(BProperty.class);
                        if (property != null) {
                            try {
                                field.setAccessible(true);
                                final Object fieldValue = field.get(value);
                                if (fieldValue != null) {
                                    dictionary.value().put(BByteString.of(property.value()), write(fieldValue));
                                }
                            } catch (IllegalAccessException e) {
                                throw new BException(e);
                            }
                        }
                    });
        } catch (Exception e) {
            throw new BException("Failed to write BDictionary", e);
        }
        return dictionary;
    }

}
