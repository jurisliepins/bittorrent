package com.github.jurisliepins.value;

import com.github.jurisliepins.BException;

import java.util.Objects;

import static com.github.jurisliepins.value.BInteger.BExceptions.unexpectedTypeException;

public record BInteger(long value) implements BValue {

    @Override
    public int hashCode() {
        return ((Long) value).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        Objects.requireNonNull(other, "other is null");
        return switch (other) {
            case BValue val -> ((Long) value).equals(val.toLong());
            default -> throw unexpectedTypeException();
        };
    }

    @Override
    public int compareTo(BValue other) {
        Objects.requireNonNull(other, "other is null");
        return ((Long) value).compareTo(other.toLong());
    }

    public static BInteger of(Byte value) {
        Objects.requireNonNull(value, "value is null");
        return of(value.shortValue());
    }

    public static BInteger of(Short value) {
        Objects.requireNonNull(value, "value is null");
        return of(value.intValue());
    }

    public static BInteger of(Integer value) {
        Objects.requireNonNull(value, "value is null");
        return of(value.longValue());
    }

    public static BInteger of(Long value) {
        Objects.requireNonNull(value, "value is null");
        return new BInteger(value);
    }

    public static class BExceptions {
        public static BException unexpectedTypeException() {
            return new BException("Unexpected type");
        }
    }

}
