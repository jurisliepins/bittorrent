package com.github.jurisliepins.value;

import com.github.jurisliepins.BException;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public record BDictionary(@NonNull Map<BValue, BValue> value) implements BValue {
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
    public int compareTo(@NonNull final BValue other) {
        throw new BException("Comparable not supported for %s".formatted(BValueType.BDictionaryType));
    }

    @Override
    public String toString() {
        return "BDictionary[value=\"\"]";
    }

    public static BDictionary of() {
        return new BDictionary(new HashMap<>());
    }

    public static BDictionary of(@NonNull final Map<BValue, BValue> values) {
        return new BDictionary(values);
    }

    public static BDictionary of(@NonNull final BValue k, @NonNull final BValue v) {
        var map = new HashMap<BValue, BValue>();
        map.put(k, v);
        return new BDictionary(map);
    }

    public static BDictionary of(
            @NonNull final BValue k1, @NonNull final BValue v1,
            @NonNull final BValue k2, @NonNull final BValue v2) {
        var map = new HashMap<BValue, BValue>();
        map.put(k1, v1);
        map.put(k2, v2);
        return new BDictionary(map);
    }

    public static BDictionary of(
            @NonNull final BValue k1, @NonNull final BValue v1,
            @NonNull final BValue k2, @NonNull final BValue v2,
            @NonNull final BValue k3, @NonNull final BValue v3) {
        var map = new HashMap<BValue, BValue>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return new BDictionary(map);
    }
}
