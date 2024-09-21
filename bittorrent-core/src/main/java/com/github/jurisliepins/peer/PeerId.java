package com.github.jurisliepins.peer;

import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import static com.github.jurisliepins.context.Context.CLIENT_ID;
import static com.github.jurisliepins.context.Context.CLIENT_VERSION;

public final class PeerId {
    private static final int ID_BYTES_LENGTH = 20;
    private static final int ID_STRING_LENGTH = 20;

    private static final long MIN_ID_VALUE = 0;
    private static final long MAX_ID_VALUE = 1_000_000_000_000L;

    public static final PeerId BLANK = new PeerId();

    private final String value;

    public PeerId(final byte @NonNull [] bytes) {
        this.value = new String(requireValid(bytes), StandardCharsets.US_ASCII);
    }

    public PeerId(@NonNull final String string) {
        this.value = requireValid(string);
    }

    private PeerId() {
        this.value = "";
    }

    public byte[] toByteArray() {
        return value.getBytes(StandardCharsets.US_ASCII);
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
            case PeerId that -> value.equals(that.value);
            default -> false;
        };
    }

    private static byte[] requireValid(final byte @NonNull [] bytes) {
        if (bytes.length != ID_BYTES_LENGTH) {
            throw new IllegalArgumentException("Bytes length must be 20 characters");
        }
        return bytes;
    }

    private static String requireValid(@NonNull final String string) {
        if (string.length() != ID_STRING_LENGTH) {
            throw new IllegalArgumentException("String length must be 20 characters");
        }
        return string;
    }

    public static PeerId self() {
        return new PeerId("-%s%s-%012d".formatted(
                CLIENT_ID,
                CLIENT_VERSION,
                new Random().nextLong(MIN_ID_VALUE, MAX_ID_VALUE)));
    }
}
