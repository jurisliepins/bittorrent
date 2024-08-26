package com.github.jurisliepins.value;

import com.github.jurisliepins.BException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record BDictionary(Map<BValue, BValue> value) implements BValue {
    public BDictionary {
        Objects.requireNonNull(value, "value is null");
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return switch (other) {
            case BDictionary val -> value().equals(val.value());
            default -> false;
        };
    }

    @Override
    public int compareTo(final BValue other) {
        throw new BException("Comparable not supported for %s".formatted(BValueType.BDictionaryType));
    }

    public static BDictionary of() {
        return new BDictionary(new HashMap<>());
    }

    public static BDictionary of(final Map<BValue, BValue> values) {
        return new BDictionary(Objects.requireNonNull(values, "values is null"));
    }

    public static BDictionary of(final BValue k, final BValue v) {
        var map = new HashMap<BValue, BValue>();
        map.put(k, v);
        return new BDictionary(map);
    }

    public static BDictionary of(
            final BValue k1, final BValue v1,
            final BValue k2, final BValue v2) {
        var map = new HashMap<BValue, BValue>();
        map.put(k1, v1);
        map.put(k2, v2);
        return new BDictionary(map);
    }

    public static BDictionary of(
            final BValue k1, final BValue v1,
            final BValue k2, final BValue v2,
            final BValue k3, final BValue v3) {
        var map = new HashMap<BValue, BValue>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return new BDictionary(map);
    }
}