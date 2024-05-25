package com.github.jurisliepins;

import com.github.jurisliepins.value.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public class BObjectMapper {

    public <T> T readFromBDictionary(BDictionary value, Class<T> clazz) {
        return (T) readBDictionary(value, clazz);
    }

    public <T> BDictionary writeToBDictionary(T value) {
        return writeBDictionary(value);
    }

    private static Object read(BValue value, Class<?> type, Class<?> genericType) {
        return switch (value) {
            case BByteString val -> readBByteString(val, type);
            case BInteger val -> readBInteger(val, type);
            case BList val -> readBList(val, type, genericType);
            case BDictionary val -> readBDictionary(val, type);
        };
    }

    private static Object readBByteString(BByteString value, Class<?> clazz) {
        return switch (clazz.getName()) {
            case "[B" -> value.toBytes();
            default -> value.toString(StandardCharsets.UTF_8);
        };
    }

    private static Object readBInteger(BInteger value, Class<?> clazz) {
        return switch (clazz.getName()) {
            case "byte", "java.lang.Byte" -> value.toByte();
            case "short", "java.lang.Short" -> value.toShort();
            case "int", "java.lang.Integer" -> value.toInteger();
            case "float", "java.lang.Float" -> (float) value.value();
            case "double", "java.lang.Double" -> (double) value.value();
            case "boolean", "java.lang.Boolean" -> switch (value.toInteger()) {
                case 1 -> true;
                case 0 -> false;
                default -> throw new BException(
                        "Cannot read value %d to boolean. Only 0 and 1 supported.".formatted(value.toInteger()));
            };
            case "char", "java.lang.Character" -> (char) value.value();
            case "java.time.OffsetDateTime" -> OffsetDateTime.ofInstant(
                    Instant.ofEpochSecond(value.toLong()), ZoneOffset.UTC);
            default -> value.toLong();
        };
    }

    private static Object readBList(BList value, Class<?> type, Class<?> genericType) {
        return switch (type.getName()) {
            case "[B" -> mapToBytes(value.toList().stream()
                    .map(BObjectMapper::readByte)
                    .collect(Collectors.toList()));
            case "[S" -> mapToShorts(value.toList().stream()
                    .map(BObjectMapper::readShort)
                    .collect(Collectors.toList()));
            case "[I" -> mapToIntegers(value.toList().stream()
                    .map(BObjectMapper::readInteger)
                    .collect(Collectors.toList()));
            case "[J" -> mapToLongs(value.toList().stream()
                    .map(BObjectMapper::readLong)
                    .collect(Collectors.toList()));
            case "[F" -> mapToFloats(value.toList().stream()
                    .map(BObjectMapper::readFloat)
                    .collect(Collectors.toList()));
            case "[D" -> mapToDoubles(value.toList().stream()
                    .map(BObjectMapper::readDouble)
                    .collect(Collectors.toList()));
            case "[Z" -> mapToBooleans(value.toList().stream()
                    .map(BObjectMapper::readBoolean)
                    .collect(Collectors.toList()));
            case "[C" -> mapToCharacters(value.toList().stream()
                    .map(BObjectMapper::readCharacter)
                    .collect(Collectors.toList()));
            default -> value.toList().stream()
                    .map(val -> read(val, genericType, genericType))
                    .collect(Collectors.toCollection(ArrayList::new));
        };
    }

    private static Object readBDictionary(BDictionary value, Class<?> clazz) {
        try {
            var values = Arrays.stream(clazz.getDeclaredFields())
                    .map(field -> {
                        var property = field.getAnnotation(BProperty.class);
                        if (property != null) {
                            var val = value.value().get(BByteString.of(property.value()));
                            if (val != null) {
                                return switch (field.getType().getTypeName()) {
                                    case "java.util.Collection",
                                         "java.util.List",
                                         "java.util.ArrayList" -> {
                                        // Because collections can be generic, we need to figure out the genetic type
                                        // and pass it along to correctly parse entries in a collection.
                                        Class<?> fieldType = field.getType();
                                        Class<?> genericFieldType = Optional.of((ParameterizedType) field.getGenericType())
                                                .flatMap(type -> Arrays.stream(type.getActualTypeArguments()).findFirst())
                                                .map(type -> switch (type) {
                                                    case ParameterizedType t -> (Class<?>) t.getRawType(); // The generic type of the collection is itself generic.
                                                    case Type t -> (Class<?>) t; // Generic type of the collection is not generic.
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

    private static Byte readByte(BValue value) {
        return (Byte) read(value, Byte.class, null);
    }

    private static Short readShort(BValue value) {
        return (Short) read(value, Short.class, null);
    }

    private static Integer readInteger(BValue value) {
        return (Integer) read(value, Integer.class, null);
    }

    private static Long readLong(BValue value) {
        return (Long) read(value, Long.class, null);
    }

    private static Boolean readBoolean(BValue value) {
        return (Boolean) read(value, Boolean.class, null);
    }

    private static Character readCharacter(BValue value) {
        return (Character) read(value, Character.class, null);
    }

    private static Float readFloat(BValue value) {
        return (Float) read(value, Float.class, null);
    }

    private static Double readDouble(BValue value) {
        return (Double) read(value, Double.class, null);
    }

    private static byte[] mapToBytes(List<Byte> value) {
        var array = new byte[value.size()];
        for (var idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
    }

    private static short[] mapToShorts(List<Short> value) {
        var array = new short[value.size()];
        for (var idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
    }

    private static int[] mapToIntegers(List<Integer> value) {
        var array = new int[value.size()];
        for (var idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
    }

    private static long[] mapToLongs(List<Long> value) {
        var array = new long[value.size()];
        for (var idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
    }

    private static boolean[] mapToBooleans(List<Boolean> value) {
        var array = new boolean[value.size()];
        for (var idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
    }

    private static char[] mapToCharacters(List<Character> value) {
        var array = new char[value.size()];
        for (var idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
    }

    private static float[] mapToFloats(List<Float> value) {
        var array = new float[value.size()];
        for (var idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
    }

    private static double[] mapToDoubles(List<Double> value) {
        var array = new double[value.size()];
        for (var idx = 0; idx < value.size(); idx++) {
            array[idx] = value.get(idx);
        }
        return array;
    }

    private static BValue write(Object value) {
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

    private static BByteString writeBByteString(byte[] value) {
        return BByteString.of(value);
    }

    private static BByteString writeBByteString(String value) {
        return BByteString.of(value);
    }

    private static BInteger writeBInteger(byte value) {
        return BInteger.of(value);
    }

    private static BInteger writeBInteger(short value) {
        return BInteger.of(value);
    }

    private static BInteger writeBInteger(int value) {
        return BInteger.of(value);
    }

    private static BInteger writeBInteger(long value) {
        return BInteger.of(value);
    }

    private static BInteger writeBInteger(float value) {
        return BInteger.of(((Float) value).longValue());
    }

    private static BInteger writeBInteger(double value) {
        return BInteger.of(((Double) value).longValue());
    }

    private static BInteger writeBInteger(boolean value) {
        return BInteger.of(value ? 1 : 0);
    }

    private static BInteger writeBInteger(char value) {
        return BInteger.of((byte) value);
    }

    private static BInteger writeBInteger(OffsetDateTime value) {
        return BInteger.of(value.toEpochSecond());
    }

    private static BList writeBList(Collection<?> value) {
        return BList.of(value.stream()
                .map(BObjectMapper::write)
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    private static BList writeBList(short[] value) {
        List<BValue> list = new ArrayList<>(value.length);
        for (var val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList writeBList(int[] value) {
        List<BValue> list = new ArrayList<>(value.length);
        for (var val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList writeBList(long[] value) {
        List<BValue> list = new ArrayList<>(value.length);
        for (var val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList writeBList(float[] value) {
        List<BValue> list = new ArrayList<>(value.length);
        for (var val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList writeBList(double[] value) {
        List<BValue> list = new ArrayList<>(value.length);
        for (var val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList writeBList(boolean[] value) {
        List<BValue> list = new ArrayList<>(value.length);
        for (var val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList writeBList(char[] value) {
        List<BValue> list = new ArrayList<>(value.length);
        for (var val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static <T> BDictionary writeBDictionary(T value) {
        var dictionary = BDictionary.of();
        try {
            Arrays.stream(value.getClass().getDeclaredFields())
                    .forEach(field -> {
                        var property = field.getAnnotation(BProperty.class);
                        if (property != null) {
                            try {
                                field.setAccessible(true);
                                dictionary.value().put(BByteString.of(property.value()), write(field.get(value)));
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
