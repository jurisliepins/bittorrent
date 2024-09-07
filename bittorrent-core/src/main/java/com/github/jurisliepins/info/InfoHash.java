package com.github.jurisliepins.info;

import lombok.NonNull;

import java.util.Arrays;
import java.util.HexFormat;

public final class InfoHash {
    private static final int BYTES_LENGTH = 20;
    private static final int STRING_LENGTH = 40;

    public static final InfoHash BLANK = new InfoHash();

    private final byte[] bytes;
    private final String string;
    private final int hashCode;

    public InfoHash(final byte @NonNull [] bytes) {
        if (bytes.length != BYTES_LENGTH) {
            throw new IllegalArgumentException("Bytes length must be %s characters".formatted(BYTES_LENGTH));
        }
        this.bytes = bytes;
        this.string = HexFormat.of().formatHex(bytes);
        this.hashCode = Arrays.hashCode(bytes);
    }

    public InfoHash(@NonNull final String string) {
        if (string.length() != STRING_LENGTH) {
            throw new IllegalArgumentException("String length must be %s characters".formatted(STRING_LENGTH));
        }
        this.string = string;
        this.bytes = HexFormat.of().parseHex(string);
        this.hashCode = Arrays.hashCode(bytes);
    }

    private InfoHash() {
        this.bytes = new byte[]{};
        this.string = "";
        this.hashCode = Arrays.hashCode(bytes);
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
        return hashCode;
    }

    @Override
    public boolean equals(final Object other) {
        return switch (other) {
            case InfoHash hash -> Arrays.equals(bytes, hash.bytes);
            default -> false;
        };
    }
}
