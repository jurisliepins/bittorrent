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
            case BInteger val -> value() == val.value();
            default -> false;
        };
    }

    @Override
    public int compareTo(final BValue other) {
        return Long.compare(value, Objects.requireNonNull(other, "other is null").toLong());
    }

    public static BInteger of(final boolean value) {
        return of(value ? 1L : 0L);
    }

    public static BInteger of(final char value) {
        return of((byte) value);
    }

    public static BInteger of(final byte value) {
        return of((short) value);
    }

    public static BInteger of(final short value) {
        return of((int) value);
    }

    public static BInteger of(final int value) {
        return of((long) value);
    }

    public static BInteger of(final long value) {
        return new BInteger(value);
    }

    public static BInteger of(final float value) {
        return of((int) value);
    }

    public static BInteger of(final double value) {
        return of((long) value);
    }
}
