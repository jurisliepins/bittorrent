package com.github.jurisliepins.value;

import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public record BByteString(byte @NonNull [] value) implements BValue {
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
    public int compareTo(@NonNull final BValue other) {
        return Arrays.compare(value, other.toBytes());
    }

    @Override
    public String toString() {
        return "BByteString[value=\"%s\"]".formatted(new String(value, StandardCharsets.UTF_8));
    }

    public static BByteString of(final byte @NonNull [] value) {
        return new BByteString(value);
    }

    public static BByteString of(@NonNull final String value) {
        return of(value.getBytes(StandardCharsets.UTF_8));
    }
}
