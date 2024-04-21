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
    public boolean equals(Object other) {
        Objects.requireNonNull(other, "other is null");
        return switch (other) {
            case BValue val -> value.equals(val.toMap());
            default -> throw new BException("Unexpected type");
        };
    }

    @Override
    public int compareTo(BValue other) {
        throw new BException("Comparable not supported for %s".formatted(BValueType.BDictionaryType));
    }

    public static BDictionary of() {
        return new BDictionary(new HashMap<>());
    }

    public static BDictionary of(Map<BValue, BValue> values) {
        Objects.requireNonNull(values, "values is null");
        return new BDictionary(values);
    }

    public static BDictionary of(BValue k, BValue v) {
        HashMap<BValue, BValue> map = new HashMap<>();
        map.put(k, v);
        return new BDictionary(map);
    }

    public static BDictionary of(BValue k1, BValue v1,
                                 BValue k2, BValue v2) {
        HashMap<BValue, BValue> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return new BDictionary(map);
    }

    public static BDictionary of(BValue k1, BValue v1,
                                 BValue k2, BValue v2,
                                 BValue k3, BValue v3) {
        HashMap<BValue, BValue> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return new BDictionary(map);
    }

    public static BDictionary of(BValue k1, BValue v1,
                                 BValue k2, BValue v2,
                                 BValue k3, BValue v3,
                                 BValue k4, BValue v4) {
        HashMap<BValue, BValue> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return new BDictionary(map);
    }

    public static BDictionary of(BValue k1, BValue v1,
                                 BValue k2, BValue v2,
                                 BValue k3, BValue v3,
                                 BValue k4, BValue v4,
                                 BValue k5, BValue v5) {
        HashMap<BValue, BValue> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return new BDictionary(map);
    }
}
