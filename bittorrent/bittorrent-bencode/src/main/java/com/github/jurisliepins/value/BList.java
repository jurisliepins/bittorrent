package com.github.jurisliepins.value;

import com.github.jurisliepins.BException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.github.jurisliepins.value.BList.BExceptions.comparableNotSupported;
import static com.github.jurisliepins.value.BList.BExceptions.unexpectedTypeException;

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
            default -> throw unexpectedTypeException();
        };
    }

    @Override
    public int compareTo(BValue other) {
        throw comparableNotSupported();
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

    public static class BExceptions {
        public static BException unexpectedTypeException() {
            return new BException("Unexpected type");
        }

        public static BException comparableNotSupported() {
            return new BException("Comparable not supported for %s".formatted(BValueType.BDictionaryType));
        }
    }

}
