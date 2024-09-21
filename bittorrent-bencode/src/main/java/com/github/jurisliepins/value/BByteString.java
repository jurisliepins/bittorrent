package com.github.jurisliepins.value;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

public record BByteString(byte[] value) implements BValue {
    public BByteString {
        requireNonNull(value, "value is null");
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public boolean equals(final Object other) {
        return switch (other) {
            case BByteString val -> Arrays.equals(value(), val.value());
            default -> false;
        };
    }

    @Override
    public int compareTo(final BValue other) {
        return Arrays.compare(value, requireNonNull(other, "other is null").toBytes());
    }

    @Override
    public String toString() {
        return "BByteString[value=\"%s\"]".formatted(new String(value, StandardCharsets.UTF_8));
    }

    public static BByteString of(final byte[] value) {
        return new BByteString(requireNonNull(value, "value is null"));
    }

    public static BByteString of(final String value) {
        return of(requireNonNull(value, "value is null").getBytes(StandardCharsets.UTF_8));
    }
}
