package com.github.jurisliepins.value;

import com.github.jurisliepins.BException;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public sealed interface BValue extends Comparable<BValue> permits BInteger, BByteString, BList, BDictionary {

    default char toCharacter() {
        return (char) toByte();
    }

    default byte toByte() {
        return (byte) toShort();
    }

    default short toShort() {
        return (short) toInteger();
    }

    default int toInteger() {
        return (int) toLong();
    }

    default long toLong() {
        return (long) toBInteger().value();
    }

    default BInteger toBInteger() {
        return switch (this) {
            case BInteger val -> val;
            default -> throw new BException("Unexpected BValue type - %s expected".formatted(BValueType.BIntegerType));
        };
    }

    default String toString(Charset encoding) {
        return new String(toBytes(), encoding);
    }

    default byte[] toBytes() {
        return toBByteString().value();
    }

    default BByteString toBByteString() {
        return switch (this) {
            case BByteString val -> val;
            default -> throw new BException("Unexpected BValue type - %s expected".formatted(BValueType.BByteStringType));
        };
    }

    default List<BValue> toList() {
        return toBList().value();
    }

    default BList toBList() {
        return switch (this) {
            case BList val -> val;
            default -> throw new BException("Unexpected BValue type - %s expected".formatted(BValueType.BListType));
        };
    }

    default Map<BValue, BValue> toMap() {
        return toBDictionary().value();
    }

    default BDictionary toBDictionary() {
        return switch (this) {
            case BDictionary val -> val;
            default -> throw new BException("Unexpected BValue type - %s expected".formatted(BValueType.BDictionaryType));
        };
    }
}
