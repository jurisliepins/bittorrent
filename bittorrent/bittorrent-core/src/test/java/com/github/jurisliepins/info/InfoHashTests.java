package com.github.jurisliepins.info;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Info-hash tests")
public final class InfoHashTests {

    private static final byte[] HASH_BYTES = new byte[]{
            (byte) 73, (byte) 65, (byte) 121, (byte) 113, (byte) 74,
            (byte) 108, (byte) 214, (byte) 39, (byte) 35, (byte) 157,
            (byte) 254, (byte) 222, (byte) 223, (byte) 45, (byte) 233,
            (byte) 239, (byte) 153, (byte) 76, (byte) 175, (byte) 3};

    private static final String HASH_STRING = "494179714a6cd627239dfededf2de9ef994caf03";

    @Test
    @DisplayName("Should create hash from bytes")
    public void shouldCreateHashFromBytes() {
        assertArrayEquals(HASH_BYTES, new InfoHash(HASH_BYTES).bytes());
    }

    @Test
    @DisplayName("Should create hash from string")
    public void shouldCreateHashFromString() {
        assertArrayEquals(HASH_BYTES, new InfoHash(HASH_STRING).bytes());
    }

    @Test
    @DisplayName("Should same hashes be equal")
    public void shouldSameHashesBeEqual() {
        assertEquals(new InfoHash(HASH_BYTES), new InfoHash(HASH_BYTES));
    }

    @Test
    @DisplayName("Should different hashes not be equal")
    public void shouldDifferentHashesNotBeEqual() {
        final byte[] otherHash = HASH_BYTES.clone();
        otherHash[0] = (byte) 0;
        otherHash[1] = (byte) 1;
        assertNotEquals(new InfoHash(HASH_BYTES), new InfoHash(otherHash));
    }

    @Test
    @DisplayName("Should same hashes hash codes be equal")
    public void shouldSameHashesHashCodesBeEqual() {
        assertEquals(new InfoHash(HASH_BYTES).hashCode(), new InfoHash(HASH_BYTES).hashCode());
    }

    @Test
    @DisplayName("Should different hashes hash codes not be equal")
    public void shouldDifferentHashesHashCodesNotBeEqual() {
        final byte[] otherHash = HASH_BYTES.clone();
        otherHash[0] = (byte) 0;
        otherHash[1] = (byte) 1;
        assertNotEquals(new InfoHash(HASH_BYTES).hashCode(), new InfoHash(otherHash).hashCode());
    }

    @Test
    @DisplayName("Should fail on malformed bytes")
    public void shouldFailOnMalformedBytes() {
        assertThrows(IllegalArgumentException.class, () -> new InfoHash(new byte[]{}));
    }

    @Test
    @DisplayName("Should fail on malformed string")
    public void shouldFailOnMalformedString() {
        assertThrows(IllegalArgumentException.class, () -> new InfoHash(""));
    }

}
