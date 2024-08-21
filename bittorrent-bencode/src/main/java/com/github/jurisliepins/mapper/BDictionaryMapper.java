package com.github.jurisliepins.mapper;

import com.github.jurisliepins.BException;
import com.github.jurisliepins.BProperty;
import com.github.jurisliepins.value.BByteString;
import com.github.jurisliepins.value.BDictionary;
import com.github.jurisliepins.value.BValue;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class BDictionaryMapper {

    public static <T> T read(final BDictionary value, final Class<T> type) {
        final Object[] args = Arrays.stream(type.getDeclaredFields())
                .map(field -> {
                    final BProperty property = field.getAnnotation(BProperty.class);
                    if (property != null) {
                        final BValue val = value.value().get(BByteString.of(property.value()));
                        if (val != null) {
                            return BObjectMapper.read(val, field.getType());
                        }
                    }
                    return null;
                })
                .toArray();
        return createInstance(type, args);
    }

    public static <T> BDictionary write(final T value) {
        final Map<BValue, BValue> values = Arrays.stream(value.getClass().getDeclaredFields())
                    .map(field -> {
                        final BProperty property = field.getAnnotation(BProperty.class);
                        if (property != null) {
                            try {
                                if (field.trySetAccessible()) {
                                    final Object fieldValue = field.get(value);
                                    if (fieldValue != null) {
                                        final BValue key = BByteString.of(property.value());
                                        final BValue val = BObjectMapper.write(fieldValue);
                                        return new SimpleEntry<>(key, val);
                                    }
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

    @SuppressWarnings("unchecked")
    private static <T> T createInstance(final Class<T> type, Object[] args) {
        try {
            return (T) Arrays.stream(type.getDeclaredConstructors())
                    .findFirst()
                    .orElseThrow(() -> new BException("Type '%s' should have an all args constructor".formatted(type.getName())))
                    .newInstance(args);
        } catch (Exception e) {
            throw new BException("Failed to create new instance of type '%s'".formatted(type.getName()), e);
        }
    }

}
