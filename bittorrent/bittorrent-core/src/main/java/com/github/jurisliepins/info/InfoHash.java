package com.github.jurisliepins.info;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public final class InfoHash {
    private final byte[] bytes;
    private final String string;

    public InfoHash(final byte[] bytes) {
        this.bytes = Objects.requireNonNull(bytes, "bytes are null");
        this.string = HexFormat.of().formatHex(bytes);
    }

    public InfoHash(final String string) {
        this.string = Objects.requireNonNull(string, "string is null");
        this.bytes = HexFormat.of().parseHex(string);
    }

    public byte[] bytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public boolean equals(final Object other) {
        return switch (other) {
            case InfoHash hash -> Arrays.equals(bytes, hash.bytes());
            default -> false;
        };
    }
}
