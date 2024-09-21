package com.github.jurisliepins.peer;

import com.github.jurisliepins.BitTorrentClient;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import static java.util.Objects.requireNonNull;

public final class Id {
    private static final int ID_LENGTH = 20;

    private static final long MIN_ID_VALUE = 0;
    private static final long MAX_ID_VALUE = 1_000_000_000_000L;

    public static final Id BLANK = new Id();

    private final String value;

    public Id(final byte @NonNull [] value) {
        this(new String(value, StandardCharsets.US_ASCII));
    }

    public Id(@NonNull final String value) {
        if (requireNonNull(value).length() != ID_LENGTH) {
            throw new IllegalArgumentException("String length must be 20 characters");
        }
        this.value = value;
    }

    private Id() {
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
            case Id that -> value.equals(that.value);
            default -> false;
        };
    }

    public static Id self() {
        return new Id("-%s%s-%012d".formatted(
                BitTorrentClient.CLIENT_ID,
                BitTorrentClient.CLIENT_VERSION,
                new Random().nextLong(MIN_ID_VALUE, MAX_ID_VALUE)));
    }
}
