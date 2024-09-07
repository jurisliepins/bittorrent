package com.github.jurisliepins.bitfield;

import lombok.NonNull;

import java.util.Arrays;

public final class Bitfield implements ImmutableBitfield {

    private final byte[] bytes;
    private final int capacity;

    public Bitfield(final int capacity) {
        // Capacity represents the number of bits this bitfield can hold. If capacity doesn't evenly divide
        // by 8 then we need an extra byte to hold these bits.
        this.bytes = (capacity % Byte.SIZE != 0)
                ? new byte[(capacity / Byte.SIZE) + 1]
                : new byte[capacity / Byte.SIZE];
        this.capacity = capacity;
    }

    public Bitfield(final byte @NonNull [] array) {
        this.bytes = Arrays.copyOf(array, array.length);
        this.capacity = Byte.SIZE * array.length;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int count() {
        var ret = 0;
        for (var b : bytes) {
            ret += bitCount(b);
        }
        return ret;
    }

    @Override
    public boolean isFull() {
        return count() == capacity();
    }

    @Override
    public boolean isEmpty() {
        return count() == 0;
    }

    @Override
    public boolean getBit(final int index) {
        if (index < 0 || index >= capacity()) {
            return false;
        }
        var idx = index / Byte.SIZE;
        var pos = index % Byte.SIZE;
        return ((bytes[idx] >> ((Byte.SIZE - 1) - pos)) & ((byte) 1)) != ((byte) 0);
    }

    public void setBit(final int index, final boolean value) {
        if (index < 0 || index >= capacity()) {
            return;
        }
        var idx = index / Byte.SIZE;
        var pos = index % Byte.SIZE;
        if (value) {
            bytes[idx] = (byte) (bytes[idx] | (((byte) 1) << ((Byte.SIZE - 1) - pos)));
        } else {
            bytes[idx] = (byte) (bytes[idx] & ~(((byte) 1) << ((Byte.SIZE - 1) - pos)));
        }
    }

    public void setBytes(final byte @NonNull [] array) {
        for (var idx = 0; idx < bytes.length; idx++) {
            if (idx < array.length) {
                bytes[idx] = array[idx];
            } else {
                bytes[idx] = (byte) 0;
            }
        }
    }

    private static int bitCount(final byte value) {
        var i = value;
        i = (byte) (i - ((i >>> 1) & 0x55555555));
        i = (byte) ((i & 0x33333333) + ((i >>> 2) & 0x33333333));
        i = (byte) ((i + (i >>> 4)) & 0x0f0f0f0f);
        return (byte) (i & 0x3f);
    }

    @Override
    public byte[] toByteArray() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public boolean equals(final Object other) {
        return switch (other) {
            case Bitfield that -> capacity() == that.capacity() && Arrays.equals(this.bytes, that.bytes);
            default -> false;
        };
    }
}