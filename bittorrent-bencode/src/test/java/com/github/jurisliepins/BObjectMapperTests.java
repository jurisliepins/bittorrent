package com.github.jurisliepins;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.HashMap;

import static com.github.jurisliepins.value.BByteString.bstr;
import static com.github.jurisliepins.value.BDictionary.bdict;
import static com.github.jurisliepins.value.BInteger.bint;
import static com.github.jurisliepins.value.BList.blist;
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
        var value = bdict(new HashMap<>() {{
            put(bstr("utf8-string"), bstr("ɄɅ ɱ ϴ ЂЃЃ"));
            put(bstr("empty-string"), bstr(""));
            put(bstr("bytes"), bstr(new byte[]{1, 2}));
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
        var value = bdict(new HashMap<>() {{
            put(bstr("max-boolean"), bint(true));
            put(bstr("min-boolean"), bint(false));
            put(bstr("max-char"), bint((byte) 'z'));
            put(bstr("min-char"), bint((byte) 'a'));
            put(bstr("max-byte"), bint(Byte.MAX_VALUE));
            put(bstr("min-byte"), bint(Byte.MIN_VALUE));
            put(bstr("max-short"), bint(Short.MAX_VALUE));
            put(bstr("min-short"), bint(Short.MIN_VALUE));
            put(bstr("max-integer"), bint(Integer.MAX_VALUE));
            put(bstr("min-integer"), bint(Integer.MIN_VALUE));
            put(bstr("max-long"), bint(Long.MAX_VALUE));
            put(bstr("min-long"), bint(Long.MIN_VALUE));
            put(bstr("max-float"), bint(Float.MAX_VALUE));
            put(bstr("min-float"), bint(Float.MIN_VALUE));
            put(bstr("max-double"), bint(Double.MAX_VALUE));
            put(bstr("min-double"), bint(Double.MIN_VALUE));
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
        var value = bdict(new HashMap<>() {{
            put(bstr("boolean-array"), blist(bint(true), bint(false)));
            put(bstr("char-array"), blist(bint((byte) 'a'), bint((byte) 'b')));
            put(bstr("short-array"), blist(bint((short) 1), bint((short) 2)));
            put(bstr("integer-array"), blist(bint(1), bint(2)));
            put(bstr("long-array"), blist(bint(1L), bint(2L)));
            put(bstr("float-array"), blist(bint(1.0f), bint(2.0f)));
            put(bstr("double-array"), blist(bint(1.0), bint(2.0)));
            put(bstr("object-array"), blist(
                    bdict(new HashMap<>() {{
                        put(bstr("value"), bint(1));
                    }}),
                    bdict(new HashMap<>() {{
                        put(bstr("value"), bint(2));
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
        var value = bdict(new HashMap<>() {{
            put(bstr("info"), bdict(new HashMap<>() {{
                put(bstr("name"), bstr("name"));
                put(bstr("piece length"), bint(512));
                put(bstr("pieces"), bstr(new byte[]{1, 2, 3}));
                put(bstr("files"), blist(
                        bdict(new HashMap<>() {{
                            put(bstr("length"), bint(123L));
                            put(bstr("path"), bstr("path"));
                        }})));
            }}));
            put(bstr("announce"), bstr("announce"));
            put(bstr("announce-list"), blist(blist(bstr("announce1"), bstr("announce2"))));
            put(bstr("creation date"), bint(OffsetDateTime.now().toEpochSecond()));
            put(bstr("comment"), bstr("Комментарий"));
            put(bstr("created by"), bstr("Пользователь"));
            put(bstr("encoding"), bstr("utf8"));
        }});
        var parsed = BObjectMapper.read(value, MetaInfo.class);
        var mapped = BObjectMapper.write(parsed);
        assertEquals(value, mapped);
    }
}
