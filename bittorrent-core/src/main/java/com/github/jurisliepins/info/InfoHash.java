package com.github.jurisliepins.info;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public final class InfoHash {
    private static final int BYTES_LENGTH = 20;
    private static final int STRING_LENGTH = 40;

    public static final InfoHash BLANK = new InfoHash();

    private final byte[] bytes;
    private final String string;

    public InfoHash(final byte[] bytes) {
        this.bytes = Objects.requireNonNull(bytes, "bytes are null");
        if (bytes.length != BYTES_LENGTH) {
            throw new IllegalArgumentException("Bytes length must be %s characters".formatted(BYTES_LENGTH));
        }
        this.string = HexFormat.of().formatHex(bytes);
    }

    public InfoHash(final String string) {
        this.string = Objects.requireNonNull(string, "string is null");
        if (string.length() != STRING_LENGTH) {
            throw new IllegalArgumentException("String length must be %s characters".formatted(STRING_LENGTH));
        }
        this.bytes = HexFormat.of().parseHex(string);
    }

    private InfoHash() {
        this.bytes = new byte[]{};
        this.string = "";
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(bytes, bytes.length);
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
            case InfoHash hash -> Arrays.equals(bytes, hash.bytes);
            default -> false;
        };
    }
}
