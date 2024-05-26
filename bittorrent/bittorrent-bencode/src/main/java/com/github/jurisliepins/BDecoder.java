package com.github.jurisliepins;

import com.github.jurisliepins.stream.BInputStream;
import com.github.jurisliepins.value.BValue;
import com.github.jurisliepins.value.BValueType;
import com.github.jurisliepins.value.BByteString;
import com.github.jurisliepins.value.BInteger;
import com.github.jurisliepins.value.BList;
import com.github.jurisliepins.value.BDictionary;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

public final class BDecoder {
    private BDecoder() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static BValue fromStream(final BInputStream value) throws IOException {
        return read(value);
    }

    public static BValue fromBytes(final byte[] value) throws IOException {
        try (var stream = new BInputStream(value)) {
            return fromStream(stream);
        }
    }

    public static BValue fromString(final String value, final Charset encoding) throws IOException {
        return fromBytes(value.getBytes(encoding));
    }

    public static BValue read(final BInputStream stream) throws IOException {
        return switch (BValueType.fromByte(stream.peekByte())) {
            case BIntegerType -> readInteger(stream);
            case BByteStringType -> readByteString(stream);
            case BListType -> readList(stream);
            case BDictionaryType -> readDictionary(stream);
        };
    }

    private static BValue readInteger(final BInputStream stream) {
        return switch (BValueType.fromByte(stream.readByte())) {
            case BIntegerType -> decodeInteger(stream);
            default -> throw new BException("Expected %s.".formatted(BValueType.BIntegerType));
        };
    }

    private static BValue readByteString(final BInputStream stream) throws IOException {
        return switch (BValueType.fromByte(stream.peekByte())) {
            case BByteStringType -> decodeByteString(stream);
            default -> throw new BException("Expected %s.".formatted(BValueType.BByteStringType));
        };
    }

    private static BValue readList(final BInputStream stream) throws IOException {
        return switch (BValueType.fromByte(stream.readByte())) {
            case BListType -> decodeList(stream);
            default -> throw new BException("Expected %s.".formatted(BValueType.BListType));
        };
    }

    private static BValue readDictionary(final BInputStream stream) throws IOException {
        return switch (BValueType.fromByte(stream.readByte())) {
            case BDictionaryType -> decodeDictionary(stream);
            default -> throw new BException("Expected %s.".formatted(BValueType.BDictionaryType));
        };
    }

    private static BValue decodeInteger(final BInputStream stream) {
        final long multiplier = 10L;

        long sign = 1L;
        long result = 0L;

        for (byte value = stream.readByte(); value != (byte) 'e'; value = stream.readByte()) {
            switch (value) {
                case '-':
                    sign = -1L;
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    result = (result * multiplier) + ((long) (value - (byte) '0'));
                    break;
                default:
                    throw new BException("Unexpected char '%c' when reading %s."
                            .formatted((char) value, BValueType.BIntegerType));
            }
        }

        return new BInteger(sign * result);
    }

    private static BValue decodeByteString(final BInputStream stream) throws IOException {
        final int multiplier = 10;

        int length = 0;

        for (byte value = stream.readByte(); value != (byte) ':'; value = stream.readByte()) {
            switch (value) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    length = (length * multiplier) + (value - (byte) '0');
                    break;
                default:
                    throw new BException("Unexpected char '%c' when reading %s."
                            .formatted((char) value, BValueType.BByteStringType));
            }
        }

        final byte[] bytes = stream.readNBytes(length);
        if (bytes.length != length) {
            throw new BException("Unexpected byte count '%d' when expected length '%d."
                    .formatted(length, bytes.length));
        }

        return new BByteString(bytes);
    }

    private static BValue decodeList(final BInputStream stream) throws IOException {
        final ArrayList<BValue> result = new ArrayList<>();

        while (stream.peekByte() != (byte) 'e') {
            final BValue val = read(stream);
            result.add(val);
        }

        if (stream.readByte() != (byte) 'e') {
            throw new BException("Missing char '%c' when reading %s.".formatted('e', BValueType.BListType));
        }

        return new BList(result);
    }

    private static BValue decodeDictionary(final BInputStream stream) throws IOException {
        final HashMap<BValue, BValue> result = new HashMap<>();

        while (stream.peekByte() != (byte) 'e') {
            final BValue key = read(stream);
            final BValue val = read(stream);
            result.put(key, val);
        }

        if (stream.readByte() != (byte) 'e') {
            throw new BException("Missing char '%c' when reading %s.".formatted('e', BValueType.BDictionaryType));
        }

        return new BDictionary(result);
    }
}
