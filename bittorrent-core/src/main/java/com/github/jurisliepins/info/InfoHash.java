package com.github.jurisliepins.info;

import lombok.NonNull;

import java.util.HexFormat;

public final class InfoHash {
    private static final int HASH_BYTES_LENGTH = 20;
    private static final int HASH_STRING_LENGTH = 40;

    public static final InfoHash BLANK = new InfoHash();

    private final String value;

    public InfoHash(final byte @NonNull [] bytes) {
        this.value = HexFormat.of().formatHex(requireValid(bytes));
    }

    public InfoHash(@NonNull final String string) {
        this.value = requireValid(string);
    }

    private InfoHash() {
        this.value = "";
    }

    public byte[] toByteArray() {
        return HexFormat.of().parseHex(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return switch (other) {
            case InfoHash that -> value.equals(that.value);
            default -> false;
        };
    }

    private static byte[] requireValid(final byte @NonNull [] bytes) {
        if (bytes.length != HASH_BYTES_LENGTH) {
            throw new IllegalArgumentException("Bytes length must be 20 characters");
        }
        return bytes;
    }

    private static String requireValid(@NonNull final String string) {
        if (string.length() != HASH_STRING_LENGTH) {
            throw new IllegalArgumentException("String length must be 40 characters");
        }
        return string;
    }
}
