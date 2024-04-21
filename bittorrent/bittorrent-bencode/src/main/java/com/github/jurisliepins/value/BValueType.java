package com.github.jurisliepins.value;

import com.github.jurisliepins.BException;

import static com.github.jurisliepins.value.BValueType.BExceptions.invalidCharException;

public enum BValueType {
    BIntegerType,
    BByteStringType,
    BListType,
    BDictionaryType;

    public static BValueType fromByte(byte value) {
        return switch (value) {
            case 'i' -> BIntegerType;
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> BByteStringType;
            case 'l' -> BListType;
            case 'd' -> BDictionaryType;
            default -> throw invalidCharException(value);
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case BIntegerType -> "BInteger";
            case BByteStringType -> "BByteString";
            case BListType -> "BList";
            case BDictionaryType -> "BDictionary";
        };
    }

    public static class BExceptions {
        public static BException invalidCharException(byte value) {
            return new BException(("Invalid BValueType char '%c'".formatted((char) value)));
        }
    }
}