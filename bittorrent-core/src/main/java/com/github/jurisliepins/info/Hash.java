package com.github.jurisliepins.info;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.HexFormat;

@EqualsAndHashCode
public final class Hash {
    private static final int HASH_LENGTH = 40;

    public static final Hash BLANK = new Hash();

    private final String value;

    public Hash(final byte @NonNull [] value) {
        this(HexFormat.of().formatHex(value));
    }

    public Hash(@NonNull final String value) {
        if (value.length() != HASH_LENGTH) {
            throw new IllegalArgumentException("String length must be 20 characters");
        }
        this.value = value;
    }

    private Hash() {
        this.value = "";
    }

    public byte[] toBytes() {
        return HexFormat.of().parseHex(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
