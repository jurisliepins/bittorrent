package com.github.jurisliepins.value;

import com.github.jurisliepins.BException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record BList(List<BValue> value) implements BValue {
    public BList {
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
            case BValue val -> value.equals(val.toList());
            default -> throw new BException("Unexpected type.");
        };
    }

    @Override
    public int compareTo(BValue other) {
        throw new BException("Comparable not supported for %s.".formatted(BValueType.BDictionaryType));
    }

    public static BList of() {
        return new BList(new ArrayList<>());
    }

    public static BList of(BValue... values) {
        Objects.requireNonNull(values, "values is null");
        return new BList(List.of(values));
    }

    public static BList of(List<BValue> values) {
        Objects.requireNonNull(values, "values is null");
        return new BList(values);
    }
}
