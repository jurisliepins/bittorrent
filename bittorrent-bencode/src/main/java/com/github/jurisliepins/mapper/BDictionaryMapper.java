package com.github.jurisliepins.mapper;

import com.github.jurisliepins.BException;
import com.github.jurisliepins.BProperty;
import com.github.jurisliepins.value.BByteString;
import com.github.jurisliepins.value.BDictionary;
import com.github.jurisliepins.value.BValue;

import java.util.Arrays;

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
