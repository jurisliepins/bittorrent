package com.github.jurisliepins.value;

import java.time.OffsetDateTime;

import static java.util.Objects.requireNonNull;

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
        return Long.compare(value, requireNonNull(other, "other is null").toLong());
    }

    @Override
    public String toString() {
        return "BInteger[value=\"%d\"]".formatted(value);
    }

    public static BInteger bint(final boolean value) {
        return bint(value ? 1L : 0L);
    }

    public static BInteger bint(final char value) {
        return bint((byte) value);
    }

    public static BInteger bint(final byte value) {
        return bint((short) value);
    }

    public static BInteger bint(final short value) {
        return bint((int) value);
    }

    public static BInteger bint(final int value) {
        return bint((long) value);
    }

    public static BInteger bint(final long value) {
        return new BInteger(value);
    }

    public static BInteger bint(final float value) {
        return bint((int) value);
    }

    public static BInteger bint(final double value) {
        return bint((long) value);
    }

    public static BInteger bint(final OffsetDateTime value) {
        return bint(value.toEpochSecond());
    }
}
