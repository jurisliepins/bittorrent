package com.github.jurisliepins.peer;

import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import static com.github.jurisliepins.context.Context.CLIENT_ID;
import static com.github.jurisliepins.context.Context.CLIENT_VERSION;

public final class PeerId {
    private static final int BYTES_LENGTH = 20;
    private static final int STRING_LENGTH = 20;

    public static final PeerId BLANK = new PeerId();

    private final byte[] bytes;
    private final String string;
    private final int hashCode;

    public PeerId(final byte @NonNull [] bytes) {
        if (bytes.length != BYTES_LENGTH) {
            throw new IllegalArgumentException("Bytes length must be %s characters".formatted(BYTES_LENGTH));
        }
        this.bytes = bytes;
        this.string = new String(bytes, StandardCharsets.US_ASCII);
        this.hashCode = Arrays.hashCode(bytes);
    }

    public PeerId(@NonNull final String string) {
        if (string.length() != STRING_LENGTH) {
            throw new IllegalArgumentException("String length must be %s characters".formatted(STRING_LENGTH));
        }
        this.string = string;
        this.bytes = string.getBytes(StandardCharsets.US_ASCII);
        this.hashCode = Arrays.hashCode(bytes);
    }

    private PeerId() {
        this.string = "";
        this.bytes = new byte[]{};
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
            case PeerId hash -> Arrays.equals(bytes, hash.bytes);
            default -> false;
        };
    }

    public static PeerId self() {
        return new PeerId("-%s%s-%012d".formatted(CLIENT_ID, CLIENT_VERSION, new Random().nextLong(0, 1_000_000_000_000L)));
    }
}
