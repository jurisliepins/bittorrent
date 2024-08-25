package com.github.jurisliepins.mapper;

import com.github.jurisliepins.BDecoder;
import com.github.jurisliepins.BEncoder;
import com.github.jurisliepins.BException;
import com.github.jurisliepins.stream.BInputStream;
import com.github.jurisliepins.stream.BOutputStream;
import com.github.jurisliepins.value.BValue;
import com.github.jurisliepins.value.BByteString;
import com.github.jurisliepins.value.BInteger;
import com.github.jurisliepins.value.BList;
import com.github.jurisliepins.value.BDictionary;

import java.io.IOException;
import java.nio.charset.Charset;

public class BObjectMapper {

    public static <T> T read(final BValue value, final Class<T> type) {
        return switch (value) {
            case BByteString val -> BByteStringMapper.read(val, type);
            case BInteger val -> BIntegerMapper.read(val, type);
            case BList val -> BListMapper.read(val, type);
            case BDictionary val -> BDictionaryMapper.read(val, type);
        };
    }

    public static <T> T fromStream(final BInputStream value, final Class<T> type) throws IOException {
        return read(BDecoder.fromStream(value), type);
    }

    public static <T> T fromBytes(final byte[] value, final Class<T> type) throws IOException {
        try (BInputStream stream = new BInputStream(value)) {
            return fromStream(stream, type);
        }
    }

    public static <T> T fromString(final String value, final Charset encoding, final Class<T> type) throws IOException {
        return fromBytes(value.getBytes(encoding), type);
    }

    public static <T> BValue write(final T value) {
        if (value.getClass().isArray()) {
            return switch (value.getClass().getName()) {
                case "[B" -> BByteStringMapper.write(value);
                case "[F" -> throw new BException("Type '%s' is not supported".formatted(float[].class.getName()));
                case "[D" -> throw new BException("Type '%s' is not supported".formatted(double[].class.getName()));
                case "[Z" -> throw new BException("Type '%s' is not supported".formatted(boolean[].class.getName()));
                default -> BListMapper.write(value);
            };    
        }
        return switch (value.getClass().getName()) {
            case "java.lang.String" -> BByteStringMapper.write(value);

            case "boolean", "java.lang.Boolean",
                 "char", "java.lang.Character",
                 "byte", "java.lang.Byte",
                 "short", "java.lang.Short",
                 "int", "java.lang.Integer",
                 "long", "java.lang.Long",
                 "float", "java.lang.Float",
                 "double", "java.lang.Double" -> BIntegerMapper.write(value);

            // TODO: Extend this?
            case "java.util.Collection",
                 "java.util.List",
                 "java.util.ArrayList" -> BListMapper.write(value);

            default -> BDictionaryMapper.write(value);
        };
    }

    public static <T> BOutputStream toStream(final T value) throws IOException {
        return BEncoder.toStream(write(value));
    }

    public static <T> byte[] toBytes(final T value) throws IOException {
        try (BOutputStream stream = toStream(value)) {
            return stream.toByteArray();
        }
    }

    public static <T> String toString(final T value, final Charset encoding) throws IOException {
        return new String(toBytes(value), encoding);
    }
}
