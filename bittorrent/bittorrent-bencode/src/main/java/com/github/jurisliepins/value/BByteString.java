package com.github.jurisliepins.value;

import com.github.jurisliepins.BException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import static com.github.jurisliepins.value.BByteString.BExceptions.unexpectedTypeException;

public record BByteString(byte[] value) implements BValue {

    public BByteString {
        Objects.requireNonNull(value, "value is null");
    }

    @Override
    public int compareTo(BValue other) {
        Objects.requireNonNull(other, "other is null");
        return Arrays.compare(value, other.toBytes());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public boolean equals(Object other) {
        Objects.requireNonNull(other, "other is null");
        return switch (other) {
            case BValue val -> Arrays.equals(value, val.toBytes());
            default -> throw unexpectedTypeException();
        };
    }

    public static BByteString of(byte[] value) {
        Objects.requireNonNull(value, "value is null");
        return new BByteString(value);
    }

    public static BByteString of(String value) {
        Objects.requireNonNull(value, "value is null");
        return of(value.getBytes(StandardCharsets.UTF_8));
    }

    public static class BExceptions {
        public static BException unexpectedTypeException() {
            return new BException("Unexpected type");
        }
    }

}
