package com.github.jurisliepins.bitfield;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Bitfield tests")
public final class BitfieldTests {

    @Test
    @DisplayName("Should bitfield get/set")
    public void shouldBitfieldGetSet() {
        var bytes = new byte[]{
                Byte.parseByte("00000001", 2),
                Byte.parseByte("00000010", 2),
                Byte.parseByte("00000011", 2),
                Byte.parseByte("00000100", 2),
        };
        var bitfield = new Bitfield(bytes);
        // Starting with a bitfield that looks like 00000001 00000010 00000011 00000100.
        assertTrue(bitfield.getBit(7));
        assertTrue(bitfield.getBit(14));
        assertTrue(bitfield.getBit(22));
        assertTrue(bitfield.getBit(23));
        assertTrue(bitfield.getBit(29));
        bitfield.setBit(4, true);
        bitfield.setBit(5, true);
        bitfield.setBit(6, true);
        bitfield.setBit(12, true);
        bitfield.setBit(13, true);
        bitfield.setBit(15, true);
        bitfield.setBit(20, true);
        bitfield.setBit(21, true);
        bitfield.setBit(28, true);
        bitfield.setBit(30, true);
        bitfield.setBit(31, true);
        // Now our bitfield is 00001111 00001111 00001111 00001111.
        assertFalse(bitfield.getBit(0));
        assertFalse(bitfield.getBit(1));
        assertFalse(bitfield.getBit(2));
        assertFalse(bitfield.getBit(3));
        assertTrue(bitfield.getBit(4));
        assertTrue(bitfield.getBit(5));
        assertTrue(bitfield.getBit(6));
        assertTrue(bitfield.getBit(7));
        assertFalse(bitfield.getBit(8));
        assertFalse(bitfield.getBit(9));
        assertFalse(bitfield.getBit(10));
        assertFalse(bitfield.getBit(11));
        assertTrue(bitfield.getBit(12));
        assertTrue(bitfield.getBit(13));
        assertTrue(bitfield.getBit(14));
        assertTrue(bitfield.getBit(15));
        assertFalse(bitfield.getBit(16));
        assertFalse(bitfield.getBit(17));
        assertFalse(bitfield.getBit(18));
        assertFalse(bitfield.getBit(19));
        assertTrue(bitfield.getBit(20));
        assertTrue(bitfield.getBit(21));
        assertTrue(bitfield.getBit(22));
        assertTrue(bitfield.getBit(23));
        assertFalse(bitfield.getBit(24));
        assertFalse(bitfield.getBit(25));
        assertFalse(bitfield.getBit(26));
        assertFalse(bitfield.getBit(27));
        assertTrue(bitfield.getBit(28));
        assertTrue(bitfield.getBit(29));
        assertTrue(bitfield.getBit(30));
        assertTrue(bitfield.getBit(21));
    }

    @Test
    @DisplayName("Should bitfield get bit count")
    public void shouldBitfieldGetBitCount() {
        var bitfield = new Bitfield(Byte.SIZE * 4);
        assertEquals(0, bitfield.count());
        bitfield.setBit(0, true);
        assertEquals(1, bitfield.count());
        bitfield.setBit(1, true);
        assertEquals(2, bitfield.count());
        bitfield.setBit(0, true);
        assertEquals(2, bitfield.count());
        bitfield.setBit(1, true);
        assertEquals(2, bitfield.count());
        bitfield.setBit(2, false);
        assertEquals(2, bitfield.count());
        bitfield.setBit(3, false);
        assertEquals(2, bitfield.count());
        bitfield.setBit(0, false);
        assertEquals(1, bitfield.count());
        bitfield.setBit(1, false);
        assertEquals(0, bitfield.count());
        bitfield.setBit(0, true);
        bitfield.setBit(1, true);
        bitfield.setBit(2, true);
        bitfield.setBit(3, true);
        bitfield.setBit(4, true);
        bitfield.setBit(5, true);
        bitfield.setBit(6, true);
        bitfield.setBit(7, true);
        bitfield.setBit(8, true);
        bitfield.setBit(9, true);
        bitfield.setBit(10, true);
        bitfield.setBit(11, true);
        bitfield.setBit(12, true);
        bitfield.setBit(13, true);
        bitfield.setBit(14, true);
        bitfield.setBit(15, true);
        assertEquals((Byte.SIZE * 2), bitfield.count());
    }

    @Test
    @DisplayName("Should bitfield get is empty")
    public void shouldBitfieldGetIsEmpty() {
        var bitfield = new Bitfield(2);
        bitfield.setBit(0, true);
        bitfield.setBit(1, true);
        assertFalse(bitfield.isEmpty());
        bitfield.setBit(0, false);
        bitfield.setBit(1, false);
        assertTrue(bitfield.isEmpty());
    }

    @Test
    @DisplayName("Should bitfield equals")
    public void shouldBitfieldEquals() {
        var bytes = new byte[]{
                Byte.parseByte("01010101", 2),
                Byte.parseByte("01010101", 2),
        };
        var bitfield1 = new Bitfield(bytes);
        var bitfield2 = new Bitfield(Byte.SIZE * bytes.length);
        bitfield2.setBit(1, true);
        bitfield2.setBit(3, true);
        bitfield2.setBit(5, true);
        bitfield2.setBit(7, true);
        bitfield2.setBit(9, true);
        bitfield2.setBit(11, true);
        bitfield2.setBit(13, true);
        bitfield2.setBit(15, true);
        assertEquals(bitfield1, bitfield2);
        bitfield1.setBit(0, true);
        bitfield1.setBit(15, false);
        assertNotEquals(bitfield1, bitfield2);
    }

    @Test
    @DisplayName("Should bitfield hash code")
    public void shouldBitfieldHashCode() {
        var bytes = new byte[]{
                Byte.parseByte("01010101", 2),
                Byte.parseByte("01010101", 2),
        };
        var bitfield1 = new Bitfield(bytes);
        var bitfield2 = new Bitfield(Byte.SIZE * bytes.length);
        bitfield2.setBit(1, true);
        bitfield2.setBit(3, true);
        bitfield2.setBit(5, true);
        bitfield2.setBit(7, true);
        bitfield2.setBit(9, true);
        bitfield2.setBit(11, true);
        bitfield2.setBit(13, true);
        bitfield2.setBit(15, true);
        assertEquals(bitfield1.hashCode(), bitfield2.hashCode());
        bitfield1.setBit(0, true);
        bitfield1.setBit(15, false);
        assertNotEquals(bitfield1.hashCode(), bitfield2.hashCode());
    }
}
