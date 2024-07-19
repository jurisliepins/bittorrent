package com.github.jurisliepins;

import com.github.jurisliepins.stream.BInputStream;
import com.github.jurisliepins.stream.BOutputStream;
import com.github.jurisliepins.value.BValue;
import com.github.jurisliepins.value.BByteString;
import com.github.jurisliepins.value.BInteger;
import com.github.jurisliepins.value.BList;
import com.github.jurisliepins.value.BDictionary;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class BObjectMapper {

    public <T> T readFromStream(final BInputStream value, final Class<T> clazz) throws IOException {
        return switch (BDecoder.fromStream(value)) {
            case BDictionary dictionary -> readFromBDictionary(dictionary, clazz);
            default -> throw new BException("Unexpected type read from stream. BDictionary expected.");
        };
    }

    public <T> T readFromBytes(final byte[] value, final Class<T> clazz) throws IOException {
        try (BInputStream stream = new BInputStream(value)) {
            return readFromStream(stream, clazz);
        }
    }

    public <T> T readFromString(final String value, final Charset encoding, final Class<T> clazz) throws IOException {
        return readFromBytes(value.getBytes(encoding), clazz);
    }

    public <T> T readFromBDictionary(final BDictionary value, final Class<T> clazz) {
        return (T) readBDictionary(value, clazz);
    }

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

    private static Object read(final BValue value, final Class<?> type, final Class<?> genericType) {
        return switch (value) {
            case BByteString val -> readBByteString(val, type);
            case BInteger val -> readBInteger(val, type);
            case BList val -> readBList(val, type, genericType);
            case BDictionary val -> readBDictionary(val, type);
        };
    }

    private static Object readBByteString(final BByteString value, final Class<?> clazz) {
        return switch (clazz.getName()) {
            case "[B" -> value.toBytes();
            default -> value.toString(StandardCharsets.UTF_8);
        };
    }

    private static Object readBInteger(final BInteger value, final Class<?> clazz) {
        return switch (clazz.getName()) {
            case "byte", "java.lang.Byte" -> value.toByte();
            case "short", "java.lang.Short" -> value.toShort();
            case "int", "java.lang.Integer" -> value.toInteger();
            case "float", "java.lang.Float" -> (float) value.value();
            case "double", "java.lang.Double" -> (double) value.value();
            case "boolean", "java.lang.Boolean" -> switch (value.toInteger()) {
                case 1 -> true;
                case 0 -> false;
                default -> throw new BException("Cannot read value %d to boolean. Only 0 and 1 supported."
                                                        .formatted(value.toInteger()));
            };
            case "char", "java.lang.Character" -> (char) value.value();
            case "java.time.OffsetDateTime" -> OffsetDateTime.ofInstant(
                    Instant.ofEpochSecond(value.toLong()), ZoneOffset.UTC);
            default -> value.toLong();
        };
    }

    private static Object readBList(final BList value, final Class<?> type, final Class<?> genericType) {
        return switch (type.getName()) {
            case "[B" -> {
                final List<Byte> list = value.toList()
                        .stream()
                        .map(BObjectMapper::readByte)
                        .collect(Collectors.toList());
                yield mapToBytes(list);
            }
            case "[S" -> {
                final List<Short> list = value.toList()
                        .stream()
                        .map(BObjectMapper::readShort)
                        .collect(Collectors.toList());
                yield mapToShorts(list);
            }
            case "[I" -> {
                final List<Integer> list = value.toList()
                        .stream()
                        .map(BObjectMapper::readInteger)
                        .collect(Collectors.toList());
                yield mapToIntegers(list);
            }
            case "[J" -> {
                final List<Long> list = value.toList()
                        .stream()
                        .map(BObjectMapper::readLong)
                        .collect(Collectors.toList());
                yield mapToLongs(list);
            }
            case "[F" -> {
                final List<Float> list = value.toList()
                        .stream()
                        .map(BObjectMapper::readFloat)
                        .collect(Collectors.toList());
                yield mapToFloats(list);
            }
            case "[D" -> {
                final List<Double> list = value.toList()
                        .stream()
                        .map(BObjectMapper::readDouble)
                        .collect(Collectors.toList());
                yield mapToDoubles(list);
            }
            case "[Z" -> {
                final List<Boolean> list = value.toList()
                        .stream()
                        .map(BObjectMapper::readBoolean)
                        .collect(Collectors.toList());
                yield mapToBooleans(list);
            }
            case "[C" -> {
                final List<Character> list = value.toList()
                        .stream()
                        .map(BObjectMapper::readCharacter)
                        .collect(Collectors.toList());
                yield mapToCharacters(list);
            }
            default -> value.toList()
                    .stream()
                    .map(val -> read(val, genericType, genericType))
                    .collect(Collectors.toCollection(ArrayList::new));
        };
    }

    private static Object readBDictionary(final BDictionary value, final Class<?> clazz) {
        try {
            final Object[] values = Arrays.stream(clazz.getDeclaredFields())
                    .map(field -> {
                        final BProperty property = field.getAnnotation(BProperty.class);
                        if (property != null) {
                            final BValue val = value.value().get(BByteString.of(property.value()));
                            if (val != null) {
                                return switch (field.getType().getTypeName()) {
                                    case "java.util.Collection",
                                         "java.util.List",
                                         "java.util.ArrayList" -> {
                                        // Because collections can be generic, we need to figure out the genetic type
                                        // and pass it along to correctly parse entries in a collection.
                                        final Class<?> fieldType = field.getType();
                                        final Class<?> genericFieldType = Arrays.stream(
                                                        ((ParameterizedType) field.getGenericType())
                                                                .getActualTypeArguments())
                                                .findFirst()
                                                .map(type -> switch (type) {
                                                    // The generic type of the collection is itself generic.
                                                    case ParameterizedType t -> (Class<?>) t.getRawType();
                                                    // Generic type of the collection is not generic.
                                                    case Type t -> (Class<?>) t;
                                                })
                                                .orElse(null);
                                        yield read(val, fieldType, genericFieldType);
                                    }
                                    default -> read(val, field.getType(), null);
                                };
                            }
                        }
                        return null;
                    })
                    .toArray();
            return clazz.getDeclaredConstructors()[0].newInstance(values);
        } catch (Exception e) {
            throw new BException("Failed to read BDictionary.", e);
        }
    }

    private static Byte readByte(final BValue value) {
        return (Byte) read(value, Byte.class, null);
    }

    private static Short readShort(final BValue value) {
        return (Short) read(value, Short.class, null);
    }

    private static Integer readInteger(final BValue value) {
        return (Integer) read(value, Integer.class, null);
    }

    private static Long readLong(final BValue value) {
        return (Long) read(value, Long.class, null);
    }

    private static Boolean readBoolean(final BValue value) {
        return (Boolean) read(value, Boolean.class, null);
    }

    private static Character readCharacter(final BValue value) {
        return (Character) read(value, Character.class, null);
    }

    private static Float readFloat(final BValue value) {
        return (Float) read(value, Float.class, null);
    }

    private static Double readDouble(final BValue value) {
        return (Double) read(value, Double.class, null);
    }

    private static byte[] mapToBytes(final List<Byte> value) {
        final byte[] array = new byte[value.size()];
        for (int idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
    }

    private static short[] mapToShorts(final List<Short> value) {
        final short[] array = new short[value.size()];
        for (int idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
    }

    private static int[] mapToIntegers(final List<Integer> value) {
        final int[] array = new int[value.size()];
        for (int idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
    }

    private static long[] mapToLongs(final List<Long> value) {
        final long[] array = new long[value.size()];
        for (int idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
    }

    private static boolean[] mapToBooleans(final List<Boolean> value) {
        final boolean[] array = new boolean[value.size()];
        for (int idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
    }

    private static char[] mapToCharacters(final List<Character> value) {
        final char[] array = new char[value.size()];
        for (int idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
    }

    private static float[] mapToFloats(final List<Float> value) {
        final float[] array = new float[value.size()];
        for (int idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
    }

    private static double[] mapToDoubles(final List<Double> value) {
        final double[] array = new double[value.size()];
        for (int idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
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
            throw new BException("Failed to write BDictionary.", e);
        }
        return dictionary;
    }

}
