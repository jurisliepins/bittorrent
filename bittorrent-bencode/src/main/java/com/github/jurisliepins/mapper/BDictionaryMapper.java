package com.github.jurisliepins.mapper;

import com.github.jurisliepins.BException;
import com.github.jurisliepins.BProperty;
import com.github.jurisliepins.value.BByteString;
import com.github.jurisliepins.value.BValue;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

public final class BDictionaryMapper {

    public static <T> T read(final BValue value, final Class<T> clazz) {
        final Map<BValue, BValue> dictionary = value.toMap();

        final Object[] values = Arrays.stream(clazz.getDeclaredFields())
                .map(field -> {
                    final BProperty property = field.getAnnotation(BProperty.class);
                    if (property != null) {
                        final BValue val = dictionary.get(BByteString.of(property.value()));
                        if (val != null) {
                            Class<?> fieldType;
                            Class<?> genericFieldType;

                            switch (field.getType().getTypeName()) {
                                case "java.util.Collection",
                                     "java.util.List",
                                     "java.util.ArrayList" -> {
                                    // Because collections can be generic, we need to figure out the genetic type
                                    // and pass it along to correctly parse entries in a collection.
                                    fieldType = field.getType();
                                    genericFieldType = Arrays.stream(
                                                    ((ParameterizedType) field.getGenericType()).getActualTypeArguments())
                                            .findFirst()
                                            .map(type -> switch (type) {
                                                // The generic type of the collection is itself generic.
                                                case ParameterizedType t -> (Class<?>) t.getRawType();
                                                // Generic type of the collection is not generic.
                                                case Type t -> (Class<?>) t;
                                            })
                                            .orElse(null);
                                }
                                default -> {
                                    fieldType = field.getType();
                                    genericFieldType = null;
                                }
                            }

                            return BObjectMapper.read(val, fieldType, genericFieldType);
                        }
                    }
                    return null;
                })
                .toArray();

        return createInstance(clazz, values);
    }

    @SuppressWarnings("unchecked")
    private static <T> T createInstance(final Class<T> clazz, Object[] values) {
        try {
            return (T) Arrays.stream(clazz.getDeclaredConstructors())
                    .findFirst()
                    .orElseThrow(() -> new BException("Type '%s' should have an all args constructor".formatted(clazz.getName())))
                    .newInstance(values);
        } catch (Exception e) {
            throw new BException("Failed to create new instance of type '%s'".formatted(clazz.getName()), e);
        }
    }

}
