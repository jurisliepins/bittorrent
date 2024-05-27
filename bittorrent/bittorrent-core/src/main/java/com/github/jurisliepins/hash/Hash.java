package com.github.jurisliepins.hash;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public final class Hash {
    private final byte[] bytes;

    public Hash(final byte[] bytes) {
        this.bytes = Objects.requireNonNull(bytes, "bytes are null");
    }

    public Hash(final String string) {
        this.bytes = HexFormat.of().parseHex(Objects.requireNonNull(string, "string is null"));
    }

    public byte[] bytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return HexFormat.of().formatHex(bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public boolean equals(final Object other) {
        return switch (Objects.requireNonNull(other, "other is null")) {
            case Hash hash -> Arrays.equals(bytes, hash.bytes());
            default -> throw new HashException("Unexpected type %s.".formatted(other));
        };
    }
}
