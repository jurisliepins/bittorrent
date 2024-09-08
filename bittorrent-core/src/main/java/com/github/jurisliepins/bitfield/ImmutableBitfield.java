package com.github.jurisliepins.bitfield;

public interface ImmutableBitfield {
    int count();

    boolean isEmpty();

    boolean getBit(int idx);
}
