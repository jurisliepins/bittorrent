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
    public boolean equals(final Object other) {
        return switch (other) {
            case BList val -> value().equals(val.value());
            default -> false;
        };
    }

    @Override
    public int compareTo(final BValue other) {
        throw new BException("Comparable not supported for %s".formatted(BValueType.BDictionaryType));
    }

    @Override
    public String toString() {
        return "BList[value=\"\"]";
    }

    public static BList of() {
        return new BList(new ArrayList<>());
    }

    public static BList of(final BValue... values) {
        return new BList(List.of(Objects.requireNonNull(values, "values is null")));
    }

    public static BList of(final List<BValue> values) {
        return new BList(Objects.requireNonNull(values, "values is null"));
    }
}
