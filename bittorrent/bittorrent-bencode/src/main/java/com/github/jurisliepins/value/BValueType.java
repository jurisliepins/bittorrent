package com.github.jurisliepins.value;

import com.github.jurisliepins.BException;

public enum BValueType {
    BIntegerType,
    BByteStringType,
    BListType,
    BDictionaryType;

    public static BValueType fromByte(final byte value) {
        return switch (value) {
            case 'i' -> BIntegerType;
            case '0',
                 '1',
                 '2',
                 '3',
                 '4',
                 '5',
                 '6',
                 '7',
                 '8',
                 '9' -> BByteStringType;
            case 'l' -> BListType;
            case 'd' -> BDictionaryType;
            default -> throw new BException(("Invalid BValueType char '%c'.".formatted((char) value)));
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
}
