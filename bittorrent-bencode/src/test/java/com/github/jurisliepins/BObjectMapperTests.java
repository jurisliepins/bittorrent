package com.github.jurisliepins;

import com.github.jurisliepins.value.BInteger;
import com.github.jurisliepins.value.BByteString;
import com.github.jurisliepins.value.BList;
import com.github.jurisliepins.value.BDictionary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.HashMap;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("BObjectMapper tests")
public final class BObjectMapperTests {

    public record BStringValues(
            @BProperty("utf8-string") String utf8String,
            @BProperty("empty-string") String emptyString,
            @BProperty("bytes") byte[] bytes
    ) {
        public BStringValues {
            requireNonNull(utf8String);
            requireNonNull(emptyString);
            requireNonNull(bytes);
        }
    }

    @Test
    @DisplayName("Should read/write string")
    public void shouldReadWriteString() {
        var value = BDictionary.of(new HashMap<>() {{
            put(BByteString.of("utf8-string"), BByteString.of("ɄɅ ɱ ϴ ЂЃЃ"));
            put(BByteString.of("empty-string"), BByteString.of(""));
            put(BByteString.of("bytes"), BByteString.of(new byte[]{1, 2}));
        }});
        var parsed = BObjectMapper.read(value, BStringValues.class);
        var mapped = BObjectMapper.write(parsed);
        assertEquals(value, mapped);
    }

    public record BIntegerValues(
            @BProperty("min-boolean") boolean minBoolean,
            @BProperty("max-boolean") boolean maxBoolean,
            @BProperty("min-char") char minChar,
            @BProperty("max-char") char maxChar,
            @BProperty("max-byte") byte maxByte,
            @BProperty("min-byte") byte minByte,
            @BProperty("max-short") short maxShort,
            @BProperty("min-short") short minShort,
            @BProperty("max-integer") int maxInteger,
            @BProperty("min-integer") int minInteger,
            @BProperty("max-long") long maxLong,
            @BProperty("min-long") long minLong,
            @BProperty("max-float") float maxFloat,
            @BProperty("min-float") float minFloat,
            @BProperty("max-double") double maxDouble,
            @BProperty("min-double") double minDouble
    ) {
        //
    }

    @Test
    @DisplayName("Should read/write integer")
    public void shouldReadWriteInteger() {
        var value = BDictionary.of(new HashMap<>() {{
            put(BByteString.of("max-boolean"), BInteger.of(true));
            put(BByteString.of("min-boolean"), BInteger.of(false));
            put(BByteString.of("max-char"), BInteger.of((byte) 'z'));
            put(BByteString.of("min-char"), BInteger.of((byte) 'a'));
            put(BByteString.of("max-byte"), BInteger.of(Byte.MAX_VALUE));
            put(BByteString.of("min-byte"), BInteger.of(Byte.MIN_VALUE));
            put(BByteString.of("max-short"), BInteger.of(Short.MAX_VALUE));
            put(BByteString.of("min-short"), BInteger.of(Short.MIN_VALUE));
            put(BByteString.of("max-integer"), BInteger.of(Integer.MAX_VALUE));
            put(BByteString.of("min-integer"), BInteger.of(Integer.MIN_VALUE));
            put(BByteString.of("max-long"), BInteger.of(Long.MAX_VALUE));
            put(BByteString.of("min-long"), BInteger.of(Long.MIN_VALUE));
            put(BByteString.of("max-float"), BInteger.of(Float.MAX_VALUE));
            put(BByteString.of("min-float"), BInteger.of(Float.MIN_VALUE));
            put(BByteString.of("max-double"), BInteger.of(Double.MAX_VALUE));
            put(BByteString.of("min-double"), BInteger.of(Double.MIN_VALUE));
        }});
        var parsed = BObjectMapper.read(value, BIntegerValues.class);
        var mapped = BObjectMapper.write(parsed);
        assertEquals(value, mapped);
    }

    public record BListValue(@BProperty("value") int value) { }

    public record BListValues(
            @BProperty("boolean-array") boolean[] booleanArray,
            @BProperty("char-array") char[] charArray,
            @BProperty("short-array") short[] shortArray,
            @BProperty("integer-array") int[] integerArray,
            @BProperty("long-array") long[] longArray,
            @BProperty("float-array") float[] floatArray,
            @BProperty("double-array") double[] doubleArray,
            @BProperty("object-array") BListValue[] objectArray
    ) {
        //
    }

    @Test
    @DisplayName("Should read/write list")
    public void shouldReadWriteList() {
        var value = BDictionary.of(new HashMap<>() {{
            put(BByteString.of("boolean-array"), BList.of(BInteger.of(true), BInteger.of(false)));
            put(BByteString.of("char-array"), BList.of(BInteger.of((byte) 'a'), BInteger.of((byte) 'b')));
            put(BByteString.of("short-array"), BList.of(BInteger.of((short) 1), BInteger.of((short) 2)));
            put(BByteString.of("integer-array"), BList.of(BInteger.of(1), BInteger.of(2)));
            put(BByteString.of("long-array"), BList.of(BInteger.of(1L), BInteger.of(2L)));
            put(BByteString.of("float-array"), BList.of(BInteger.of(1.0f), BInteger.of(2.0f)));
            put(BByteString.of("double-array"), BList.of(BInteger.of(1.0), BInteger.of(2.0)));
            put(BByteString.of("object-array"), BList.of(
                    BDictionary.of(new HashMap<>() {{
                        put(BByteString.of("value"), BInteger.of(1));
                    }}),
                    BDictionary.of(new HashMap<>() {{
                        put(BByteString.of("value"), BInteger.of(2));
                    }})));
        }});
        var parsed = BObjectMapper.read(value, BListValues.class);
        var mapped = BObjectMapper.write(parsed);
        assertEquals(value, mapped);
    }

    public record File(
            @BProperty("length") long length,
            @BProperty("path") String path
    ) {
        //
    }

    public record Info(
            @BProperty("name") String name,
            @BProperty("piece length") int pieceLength,
            @BProperty("pieces") byte[] pieces,
            @BProperty("files") File[] files
    ) {
        //
    }

    public record MetaInfo(
            @BProperty("announce") String announce,
            @BProperty("announce-list") String[][] announceList,
            @BProperty("creation date") long creationDate,
            @BProperty("comment") String comment,
            @BProperty("created by") String createdBy,
            @BProperty("encoding") String encoding,
            @BProperty("info") Info info
    ) {
        //
    }

    @Test
    @DisplayName("Should read/write dictionary")
    public void shouldReadWriteDictionary() {
        var value = BDictionary.of(new HashMap<>() {{
            put(BByteString.of("info"), BDictionary.of(new HashMap<>() {{
                put(BByteString.of("name"), BByteString.of("name"));
                put(BByteString.of("piece length"), BInteger.of(512));
                put(BByteString.of("pieces"), BByteString.of(new byte[]{1, 2, 3}));
                put(BByteString.of("files"), BList.of(
                        BDictionary.of(new HashMap<>() {{
                            put(BByteString.of("length"), BInteger.of(123L));
                            put(BByteString.of("path"), BByteString.of("path"));
                        }})));
            }}));
            put(BByteString.of("announce"), BByteString.of("announce"));
            put(BByteString.of("announce-list"), BList.of(BList.of(BByteString.of("announce1"), BByteString.of("announce2"))));
            put(BByteString.of("creation date"), BInteger.of(OffsetDateTime.now().toEpochSecond()));
            put(BByteString.of("comment"), BByteString.of("Комментарий"));
            put(BByteString.of("created by"), BByteString.of("Пользователь"));
            put(BByteString.of("encoding"), BByteString.of("utf8"));
        }});
        var parsed = BObjectMapper.read(value, MetaInfo.class);
        var mapped = BObjectMapper.write(parsed);
        assertEquals(value, mapped);
    }
}
