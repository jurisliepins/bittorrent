package com.github.jurisliepins;

import com.github.jurisliepins.value.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BObjectMapper {

    public <T> T readFromBDictionary(BDictionary value, Class<T> clazz) {
        return (T) readBDictionary(value, clazz);
    }

    public <T> BDictionary writeToBDictionary(T value) {
        return writeBDictionary(value);
    }

    private static Object read(BValue value, Class<?> clazz) {
        return switch (value) {
            case BByteString val -> readBByteString(val, clazz);
            case BInteger val -> readBInteger(val, clazz);
            case BList val -> readBList(val, clazz);
            case BDictionary val -> readBDictionary(val, clazz);
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

    private static Object readBList(BList value, Class<?> clazz) {
        return switch (clazz.getName()) {
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
                    .map(val -> read(val, Object.class))
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
                                return read(val, field.getType());
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
        return (Byte) read(value, Byte.class);
    }

    private static Short readShort(BValue value) {
        return (Short) read(value, Short.class);
    }

    private static Integer readInteger(BValue value) {
        return (Integer) read(value, Integer.class);
    }

    private static Long readLong(BValue value) {
        return (Long) read(value, Long.class);
    }

    private static Boolean readBoolean(BValue value) {
        return (Boolean) read(value, Boolean.class);
    }

    private static Character readCharacter(BValue value) {
        return (Character) read(value, Character.class);
    }

    private static Float readFloat(BValue value) {
        return (Float) read(value, Float.class);
    }

    private static Double readDouble(BValue value) {
        return (Double) read(value, Double.class);
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
            case "[B" -> BByteString.of((byte[]) value);
            case "java.lang.String" -> BByteString.of((String) value);
            case "byte", "java.lang.Byte" -> BInteger.of((byte) value);
            case "short", "java.lang.Short" -> BInteger.of((short) value);
            case "int", "java.lang.Integer" -> BInteger.of((int) value);
            case "long", "java.lang.Long" -> BInteger.of((long) value);
            case "float", "java.lang.Float" -> BInteger.of(((Float) value).longValue());
            case "double", "java.lang.Double" -> BInteger.of(((Double) value).longValue());
            case "boolean", "java.lang.Boolean" -> BInteger.of(((boolean) value) ? 1 : 0);
            case "char", "java.lang.Character" -> BInteger.of((byte) ((char) value));
            case "java.time.OffsetDateTime" -> BInteger.of(((OffsetDateTime) value).toEpochSecond());
            case "[S" -> mapBList((short[]) value);
            case "[I" -> mapBList((int[]) value);
            case "[J" -> mapBList((long[]) value);
            case "[F" -> mapBList((float[]) value);
            case "[D" -> mapBList((double[]) value);
            case "[Z" -> mapBList((boolean[]) value);
            case "[C" -> mapBList((char[]) value);
            case "java.util.Collection",
                 "java.util.List",
                 "java.util.ArrayList" -> BList.of(((Collection<?>) value).stream()
                    .map(BObjectMapper::write)
                    .collect(Collectors.toCollection(ArrayList::new)));
            default -> writeBDictionary(value);
        };
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

    private static BList mapBList(short[] value) {
        List<BValue> list = new ArrayList<>(value.length);
        for (var val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList mapBList(int[] value) {
        List<BValue> list = new ArrayList<>(value.length);
        for (var val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList mapBList(long[] value) {
        List<BValue> list = new ArrayList<>(value.length);
        for (var val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList mapBList(float[] value) {
        List<BValue> list = new ArrayList<>(value.length);
        for (var val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList mapBList(double[] value) {
        List<BValue> list = new ArrayList<>(value.length);
        for (var val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList mapBList(boolean[] value) {
        List<BValue> list = new ArrayList<>(value.length);
        for (var val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }

    private static BList mapBList(char[] value) {
        List<BValue> list = new ArrayList<>(value.length);
        for (var val : value) {
            list.add(write(val));
        }
        return BList.of(list);
    }
}
