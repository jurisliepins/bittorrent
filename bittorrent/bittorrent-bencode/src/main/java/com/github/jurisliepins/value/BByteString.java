package com.github.jurisliepins.value;

import com.github.jurisliepins.BException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public record BByteString(byte[] value) implements BValue {
    public BByteString {
        Objects.requireNonNull(value, "value is null");
    }

    @Override
    public int compareTo(final BValue other) {
        return Arrays.compare(value, Objects.requireNonNull(other, "other is null").toBytes());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public boolean equals(final Object other) {
        return switch (Objects.requireNonNull(other, "other is null")) {
            case BValue val -> Arrays.equals(value, val.toBytes());
            default -> throw new BException("Unexpected type %s.".formatted(other));
        };
    }

    public static BByteString of(final byte[] value) {
        return new BByteString(Objects.requireNonNull(value, "value is null"));
    }

    public static BByteString of(final String value) {
        return of(Objects.requireNonNull(value, "value is null").getBytes(StandardCharsets.UTF_8));
    }
}
