package com.github.jurisliepins.bitfield;

import lombok.NonNull;

import java.nio.ByteBuffer;
import java.util.BitSet;

public final class Bitfield implements ImmutableBitfield {

    private final BitSet bits;

    public Bitfield() {
        this.bits = new BitSet(0);
    }

    public Bitfield(final int capacity) {
        this.bits = new BitSet(capacity);
    }

    public Bitfield(final byte @NonNull [] array) {
        this.bits = BitSet.valueOf(ByteBuffer.wrap(reverse(array)));
    }

    @Override
    public int count() {
        return bits.cardinality();
    }

    @Override
    public boolean isEmpty() {
        return count() == 0;
    }

    @Override
    public boolean getBit(final int idx) {
        return bits.get(idx);
    }

    public void setBit(final int idx, final boolean value) {
        bits.set(idx, value);
    }

    @Override
    public int hashCode() {
        return bits.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return switch (other) {
            case Bitfield that -> bits.equals(that.bits);
            default -> false;
        };
    }

    private static byte[] reverse(final byte[] array) {
        var reversed = new byte[array.length];
        for (var i = 0; i < array.length; i++) {
            reversed[i] = reverse(array[i]);
        }
        return reversed;
    }

    private static byte reverse(final byte value) {
        byte b = 0;
        byte y = value;
        for (int i = 0; i < 8; ++i) {
            b <<= 1;
            b |= (byte) (y & 1);
            y >>= 1;
        }
        return b;
    }
}
