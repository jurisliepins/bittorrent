package com.github.jurisliepins.value;

import java.util.Objects;

public record BInteger(long value) implements BValue {
    @Override
    public int hashCode() {
        return ((Long) value).hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return switch (other) {
            case BValue val -> ((Long) value).equals(val.toLong());
            default -> false;
        };
    }

    @Override
    public int compareTo(final BValue other) {
        return ((Long) value).compareTo(Objects.requireNonNull(other, "other is null").toLong());
    }

    public static BInteger of(final Byte value) {
        return of(Objects.requireNonNull(value, "value is null").shortValue());
    }

    public static BInteger of(final Short value) {
        return of(Objects.requireNonNull(value, "value is null").intValue());
    }

    public static BInteger of(final Integer value) {
        return of(Objects.requireNonNull(value, "value is null").longValue());
    }

    public static BInteger of(final Long value) {
        return new BInteger(Objects.requireNonNull(value, "value is null"));
    }
}
