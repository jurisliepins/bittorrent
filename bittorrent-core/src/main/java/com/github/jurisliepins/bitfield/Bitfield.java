package com.github.jurisliepins.bitfield;

import lombok.NonNull;

import java.nio.ByteBuffer;
import java.util.BitSet;

public final class Bitfield implements ImmutableBitfield {

    public static final Bitfield BLANK = new Bitfield();

    private final BitSet bits;

    public Bitfield() {
        bits = new BitSet(0);
    }

    public Bitfield(final int capacity) {
        bits = new BitSet(capacity);
    }

    public Bitfield(final byte @NonNull [] array) {
        var reversed = new byte[array.length];
        for (var i = 0; i < array.length; i++) {
            byte x = 0;
            byte y = array[i];
            for (var j = 0; j < Byte.SIZE; ++j) {
                x <<= 1;
                x |= (byte) (y & 1);
                y >>= 1;
            }
            reversed[i] = x;
        }
        bits = BitSet.valueOf(ByteBuffer.wrap(reversed));
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
}
