package com.github.jurisliepins.value;

import com.github.jurisliepins.BException;

import java.util.Objects;

public record BInteger(long value) implements BValue {
    @Override
    public int hashCode() {
        return ((Long) value).hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        Objects.requireNonNull(other, "other is null");
        return switch (other) {
            case BValue val -> ((Long) value).equals(val.toLong());
            default -> throw new BException("Unexpected type.");
        };
    }

    @Override
    public int compareTo(final BValue other) {
        Objects.requireNonNull(other, "other is null");
        return ((Long) value).compareTo(other.toLong());
    }

    public static BInteger of(final Byte value) {
        Objects.requireNonNull(value, "value is null");
        return of(value.shortValue());
    }

    public static BInteger of(final Short value) {
        Objects.requireNonNull(value, "value is null");
        return of(value.intValue());
    }

    public static BInteger of(final Integer value) {
        Objects.requireNonNull(value, "value is null");
        return of(value.longValue());
    }

    public static BInteger of(final Long value) {
        Objects.requireNonNull(value, "value is null");
        return new BInteger(value);
    }
}
