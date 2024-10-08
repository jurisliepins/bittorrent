package com.github.jurisliepins.value;

import com.github.jurisliepins.BException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public record BDictionary(Map<BValue, BValue> value) implements BValue {
    public BDictionary {
        requireNonNull(value, "value is null");
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

    @Override
    public String toString() {
        return "BDictionary[value=[%s]]".formatted(
                value.entrySet()
                        .stream()
                        .map(entry -> "(%s, %s)".formatted(
                                entry.getKey(),
                                entry.getValue()))
                        .collect(Collectors.joining(", ")));
    }

    public static BDictionary bdict() {
        return new BDictionary(new HashMap<>());
    }

    public static BDictionary bdict(final Map<BValue, BValue> values) {
        return new BDictionary(requireNonNull(values, "values is null"));
    }

    public static BDictionary bdict(final BValue k, final BValue v) {
        var map = new HashMap<BValue, BValue>();
        map.put(k, v);
        return new BDictionary(map);
    }

    public static BDictionary bdict(
            final BValue k1, final BValue v1,
            final BValue k2, final BValue v2) {
        var map = new HashMap<BValue, BValue>();
        map.put(k1, v1);
        map.put(k2, v2);
        return new BDictionary(map);
    }

    public static BDictionary bdict(
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
