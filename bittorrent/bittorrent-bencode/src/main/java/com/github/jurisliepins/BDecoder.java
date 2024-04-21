package com.github.jurisliepins;

import com.github.jurisliepins.stream.BInputStream;
import com.github.jurisliepins.value.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import static com.github.jurisliepins.BDecoder.BExceptions.*;

public class BDecoder {

    private static final byte I_BYTE = 'i';
    private static final byte S_BYTE = ':';
    private static final byte L_BYTE = 'l';
    private static final byte D_BYTE = 'd';
    private static final byte E_BYTE = 'e';

    public static BValue fromStream(BInputStream value) throws IOException {
        return read(value);
    }

    public static BValue fromBytes(byte[] value) throws IOException {
        try (var stream = new BInputStream(value)) {
            return fromStream(stream);
        }
    }

    public static BValue fromString(String value, Charset encoding) throws IOException {
        return fromBytes(value.getBytes(encoding));
    }

    public static BValue read(BInputStream stream) throws IOException {
        return switch (BValueType.fromByte(stream.peekByte())) {
            case BIntegerType -> readInteger(stream);
            case BByteStringType -> readByteString(stream);
            case BListType -> readList(stream);
            case BDictionaryType -> readDictionary(stream);
        };
    }

    private static BValue readInteger(BInputStream stream) {
        switch (BValueType.fromByte(stream.readByte())) {
            case BIntegerType -> {
                var sign = 1L;
                var result = 0L;

                byte value;
                while ((value = stream.readByte()) != E_BYTE) {
                    switch (value) {
                        case '-' -> sign = -1L;
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' ->
                                result = (result * 10L) + ((long) (value - (byte) '0'));
                        default -> throw unexpectedCharException(value, BValueType.BIntegerType);
                    }
                }

                return new BInteger(sign * result);
            }
            default -> throw unexpectedTypeException(BValueType.BIntegerType);
        }
    }

    private static BValue readByteString(BInputStream stream) throws IOException {
        switch (BValueType.fromByte(stream.peekByte())) {
            case BByteStringType -> {
                var length = 0;

                byte value;
                while ((value = stream.readByte()) != S_BYTE) {
                    switch (value) {
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' ->
                                length = (length * 10) + ((int) (value - (byte) '0'));
                        default -> throw unexpectedCharException(value, BValueType.BByteStringType);
                    }
                }
                byte[] bytes = stream.readNBytes(length);
                if (bytes.length != length) {
                    throw unexpectedByteCountException(length, bytes.length);
                }

                return new BByteString(bytes);
            }
            default -> throw unexpectedTypeException(BValueType.BByteStringType);
        }
    }

    private static BValue readList(BInputStream stream) throws IOException {
        switch (BValueType.fromByte(stream.readByte())) {
            case BListType -> {
                var result = new ArrayList<BValue>();

                while (stream.peekByte() != E_BYTE) {
                    var val = read(stream);
                    result.add(val);
                }
                if (stream.readByte() != E_BYTE) {
                    throw missingCharException(E_BYTE, BValueType.BListType);
                }

                return new BList(result);
            }
            default -> throw unexpectedTypeException(BValueType.BListType);
        }
    }

    private static BValue readDictionary(BInputStream stream) throws IOException {
        switch (BValueType.fromByte(stream.readByte())) {
            case BDictionaryType -> {
                var result = new HashMap<BValue, BValue>();

                while (stream.peekByte() != E_BYTE) {
                    var key = read(stream);
                    var val = read(stream);
                    result.put(key, val);
                }
                if (stream.readByte() != E_BYTE) {
                    throw missingCharException(E_BYTE, BValueType.BDictionaryType);
                }

                return new BDictionary(result);
            }
            default -> throw unexpectedTypeException(BValueType.BDictionaryType);
        }
    }

    public static class BExceptions {
        public static BException unexpectedTypeException(BValueType type) {
            return new BException("Expected %s".formatted(type));
        }

        public static BException unexpectedCharException(byte value, BValueType type) {
            return new BException("Unexpected char '%c' when reading %s".formatted((char) value, type));
        }

        public static BException missingCharException(byte value, BValueType type) {
            return new BException("Missing char '%c' when reading %s".formatted((char) value, type));
        }

        public static BException unexpectedByteCountException(int expected, int actual) {
            return new BException("Unexpected byte count '%d' when expected length '%d".formatted(actual, expected));
        }
    }

}
