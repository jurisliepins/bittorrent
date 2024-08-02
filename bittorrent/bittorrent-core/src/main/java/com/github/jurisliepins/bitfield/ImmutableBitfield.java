package com.github.jurisliepins.bitfield;

public interface ImmutableBitfield {
    int capacity();

    int count();

    boolean isFull();

    boolean isEmpty();

    boolean getBit(int index);

    byte[] toByteArray();
}
