package com.github.jurisliepins;

import com.github.jurisliepins.stream.BInputStream;
import com.github.jurisliepins.value.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

public class BDecoder {
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
        return switch (BValueType.fromByte(stream.readByte())) {
            case BIntegerType -> decodeInteger(stream);
            default -> throw new BException("Expected %s.".formatted(BValueType.BIntegerType));
        };
    }

    private static BValue readByteString(BInputStream stream) throws IOException {
        return switch (BValueType.fromByte(stream.peekByte())) {
            case BByteStringType -> decodeByteString(stream);
            default -> throw new BException("Expected %s.".formatted(BValueType.BByteStringType));
        };
    }

    private static BValue readList(BInputStream stream) throws IOException {
        return switch (BValueType.fromByte(stream.readByte())) {
            case BListType -> decodeList(stream);
            default -> throw new BException("Expected %s.".formatted(BValueType.BListType));
        };
    }

    private static BValue readDictionary(BInputStream stream) throws IOException {
        return switch (BValueType.fromByte(stream.readByte())) {
            case BDictionaryType -> decodeDictionary(stream);
            default -> throw new BException("Expected %s.".formatted(BValueType.BDictionaryType));
        };
    }

    private static BValue decodeInteger(BInputStream stream) {
        var sign = 1L;
        var result = 0L;

        for (byte value = stream.readByte(); value != (byte) 'e'; value = stream.readByte()) {
            switch (value) {
                case '-' -> sign = -1L;
                case '0',
                     '1',
                     '2',
                     '3',
                     '4',
                     '5',
                     '6',
                     '7',
                     '8',
                     '9' -> result = (result * 10L) + ((long) (value - (byte) '0'));
                default -> throw new BException(
                        "Unexpected char '%c' when reading %s.".formatted((char) value, BValueType.BIntegerType));
            }
        }

        return new BInteger(sign * result);
    }

    private static BValue decodeByteString(BInputStream stream) throws IOException {
        var length = 0;

        for (byte value = stream.readByte(); value != (byte) ':'; value = stream.readByte()) {
            switch (value) {
                case '0',
                     '1',
                     '2',
                     '3',
                     '4',
                     '5',
                     '6',
                     '7',
                     '8',
                     '9' -> length = (length * 10) + (value - (byte) '0');
                default -> throw new BException(
                        "Unexpected char '%c' when reading %s.".formatted((char) value, BValueType.BByteStringType));
            }
        }

        var bytes = stream.readNBytes(length);
        if (bytes.length != length) {
            throw new BException(
                    "Unexpected byte count '%d' when expected length '%d.".formatted(length, bytes.length));
        }

        return new BByteString(bytes);
    }

    private static BValue decodeList(BInputStream stream) throws IOException {
        var result = new ArrayList<BValue>();

        while (stream.peekByte() != (byte) 'e') {
            var val = read(stream);
            result.add(val);
        }

        if (stream.readByte() != (byte) 'e') {
            throw new BException(
                    "Missing char '%c' when reading %s.".formatted('e', BValueType.BListType));
        }

        return new BList(result);
    }

    private static BValue decodeDictionary(BInputStream stream) throws IOException {
        var result = new HashMap<BValue, BValue>();

        while (stream.peekByte() != (byte) 'e') {
            var key = read(stream);
            var val = read(stream);
            result.put(key, val);
        }

        if (stream.readByte() != (byte) 'e') {
            throw new BException(
                    "Missing char '%c' when reading %s.".formatted('e', BValueType.BDictionaryType));
        }

        return new BDictionary(result);
    }
}
