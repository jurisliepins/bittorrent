package com.github.jurisliepins.peer;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import static com.github.jurisliepins.BitTorrentClient.CLIENT_ID;
import static com.github.jurisliepins.BitTorrentClient.CLIENT_VERSION;

@EqualsAndHashCode
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
        if (value.length() != ID_LENGTH) {
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

    public static Id self() {
        return new Id("-%s%s-%012d".formatted(CLIENT_ID, CLIENT_VERSION, new Random().nextLong(MIN_ID_VALUE, MAX_ID_VALUE)));
    }
}
