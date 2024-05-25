package com.github.jurisliepins;

import com.github.jurisliepins.value.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("BObjectMapper tests")
public class BObjectMapperTests {

    public record BStringValues(
            @BProperty("utf8-string") String utf8String,
            @BProperty("empty-string") String emptyString,
            @BProperty("bytes") byte[] bytes
    ) {}

    @Test
    @DisplayName("Should read/write string")
    public void shouldReadWriteString() {
        var value = BDictionary.of(new HashMap<>() {{
            put(BByteString.of("utf8-string"), BByteString.of("ɄɅ ɱ ϴ ЂЃЃ"));
            put(BByteString.of("empty-string"), BByteString.of(""));
            put(BByteString.of("bytes"), BByteString.of(new byte[]{ 1, 2, 3 }));
        }});
        var mapper = new BObjectMapper();
        var parsed = mapper.readFromBDictionary(value, BStringValues.class);
        var mapped = mapper.writeToBDictionary(parsed);
        assertEquals(value, mapped);
    }

    public record BIntegerValues(
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
            @BProperty("min-double") double minDouble,
            @BProperty("boolean-true") boolean booleanTrue,
            @BProperty("boolean-false") boolean booleanFalse,
            @BProperty("max-char") char maxChar,
            @BProperty("min-char") char minChar,
            @BProperty("epoch-seconds") OffsetDateTime epochSeconds
    ) {}

    @Test
    @DisplayName("Should read/write integer")
    public void shouldReadWriteInteger() {
        var value = BDictionary.of(new HashMap<>() {{
            put(BByteString.of("max-byte"), BInteger.of(Byte.MAX_VALUE));
            put(BByteString.of("min-byte"), BInteger.of(Byte.MIN_VALUE));
            put(BByteString.of("max-short"), BInteger.of(Short.MAX_VALUE));
            put(BByteString.of("min-short"), BInteger.of(Short.MIN_VALUE));
            put(BByteString.of("max-integer"), BInteger.of(Integer.MAX_VALUE));
            put(BByteString.of("min-integer"), BInteger.of(Integer.MIN_VALUE));
            put(BByteString.of("max-long"), BInteger.of(Long.MAX_VALUE));
            put(BByteString.of("min-long"), BInteger.of(Long.MIN_VALUE));
            put(BByteString.of("max-float"), BInteger.of(1));
            put(BByteString.of("min-float"), BInteger.of(0));
            put(BByteString.of("max-double"), BInteger.of(1));
            put(BByteString.of("min-double"), BInteger.of(0));
            put(BByteString.of("boolean-true"), BInteger.of(1));
            put(BByteString.of("boolean-false"), BInteger.of(0));
            put(BByteString.of("max-char"), BInteger.of((byte) 'z'));
            put(BByteString.of("min-char"), BInteger.of((byte) 'a'));
            put(BByteString.of("epoch-seconds"), BInteger.of(OffsetDateTime.now().toEpochSecond()));
        }});
        var mapper = new BObjectMapper();
        var parsed = mapper.readFromBDictionary(value, BIntegerValues.class);
        var mapped = mapper.writeToBDictionary(parsed);
        assertEquals(value, mapped);
    }

    public record BListValues(
//            Skipping byte array because byte arrays are written as BByteString.
//            @BProperty("byte-array") byte[] byteArray,
            @BProperty("byte-list") List<Byte> byteList,
            @BProperty("short-array") short[] shortArray,
            @BProperty("short-list") List<Short> shortList,
            @BProperty("integer-array") int[] integerArray,
            @BProperty("integer-list") List<Integer> integerList,
            @BProperty("long-array") long[] longArray,
            @BProperty("long-list") List<Long> longList,
            @BProperty("float-array") float[] floatArray,
            @BProperty("float-list") List<Float> floatList,
            @BProperty("double-array") double[] doubleArray,
            @BProperty("double-list") List<Double> doubleList,
            @BProperty("boolean-array") boolean[] booleanArray,
            @BProperty("boolean-list") List<Boolean> booleanList,
            @BProperty("char-array") char[] charArray,
            @BProperty("char-list") List<Character> charList
    ) {}

    @Test
    @DisplayName("Should read/write list")
    public void shouldReadWriteList() {
        var value = BDictionary.of(new HashMap<>() {{
//            Skipping byte array because byte arrays are written as BByteString.
//            put(BByteString.of("byte-array"), BList.of(
//                    BInteger.of((byte) 1),
//                    BInteger.of((byte) 2),
//                    BInteger.of((byte) 3)
//            ));
            put(BByteString.of("byte-list"), BList.of(
                    BInteger.of((byte) 1),
                    BInteger.of((byte) 2),
                    BInteger.of((byte) 3)
            ));
            put(BByteString.of("short-array"), BList.of(
                    BInteger.of((short) 1),
                    BInteger.of((short) 2),
                    BInteger.of((short) 3)
            ));
            put(BByteString.of("short-list"), BList.of(
                    BInteger.of((short) 1),
                    BInteger.of((short) 2),
                    BInteger.of((short) 3)
            ));
            put(BByteString.of("integer-array"), BList.of(
                    BInteger.of(1),
                    BInteger.of(2),
                    BInteger.of(3)
            ));
            put(BByteString.of("integer-list"), BList.of(
                    BInteger.of(1),
                    BInteger.of(2),
                    BInteger.of(3)
            ));
            put(BByteString.of("long-array"), BList.of(
                    BInteger.of(1),
                    BInteger.of(2),
                    BInteger.of(3)
            ));
            put(BByteString.of("long-list"), BList.of(
                    BInteger.of(1),
                    BInteger.of(2),
                    BInteger.of(3)
            ));
            put(BByteString.of("float-array"), BList.of(
                    BInteger.of(1),
                    BInteger.of(2),
                    BInteger.of(3)
            ));
            put(BByteString.of("float-list"), BList.of(
                    BInteger.of(1),
                    BInteger.of(2),
                    BInteger.of(3)
            ));
            put(BByteString.of("double-array"), BList.of(
                    BInteger.of(1),
                    BInteger.of(2),
                    BInteger.of(3)
            ));
            put(BByteString.of("double-list"), BList.of(
                    BInteger.of(1),
                    BInteger.of(2),
                    BInteger.of(3)
            ));
            put(BByteString.of("boolean-array"), BList.of(
                    BInteger.of(0),
                    BInteger.of(1)
            ));
            put(BByteString.of("boolean-list"), BList.of(
                    BInteger.of(0),
                    BInteger.of(1)
            ));
            put(BByteString.of("char-array"), BList.of(
                    BInteger.of((byte) 'a'),
                    BInteger.of((byte) 'b'),
                    BInteger.of((byte) 'c')
            ));
            put(BByteString.of("char-list"), BList.of(
                    BInteger.of((byte) 'a'),
                    BInteger.of((byte) 'b'),
                    BInteger.of((byte) 'c')
            ));
        }});
        var mapper = new BObjectMapper();
        var parsed = mapper.readFromBDictionary(value, BListValues.class);
        var mapped = mapper.writeToBDictionary(parsed);
        assertEquals(value, mapped);
    }

    public record File(
            @BProperty("length") long length,
            @BProperty("md5sum") String md5sum,
            @BProperty("path") List<String> path
    ) {
        public File {
            Objects.requireNonNull(path, "path is null");
        }
    }

    public record Info(
            @BProperty("piece length") int pieceLength,
            @BProperty("pieces") byte[] pieces,
            @BProperty("private") Boolean isPrivate,
            @BProperty("name") String name,
            @BProperty("length") long length,
            @BProperty("md5sum") String md5sum,
            @BProperty("files") List<File> files
    ) {
        public Info {
            Objects.requireNonNull(pieces, "pieces is null");
            Objects.requireNonNull(name, "name is null");
        }
    }

    public record MetaInfo(
            @BProperty("info") Info info,
            @BProperty("announce") String announce,
            @BProperty("announce-list") List<List<String>> announceList,
            @BProperty("creation date") OffsetDateTime creationDate,
            @BProperty("comment") String comment,
            @BProperty("created by") String createdBy,
            @BProperty("encoding") String encoding
    ) {
        public MetaInfo {
            Objects.requireNonNull(info, "info is null");
            Objects.requireNonNull(announce, "announce is null");
        }
    }

    @Test
    @DisplayName("Should read/write meta-info")
    public void shouldReadWriteDictionary() {
        var value = BDictionary.of(new HashMap<>() {{
            put(BByteString.of("info"), BDictionary.of(new HashMap<>() {{
                put(BByteString.of("piece length"), BInteger.of(1));
                put(BByteString.of("pieces"), BByteString.of(new byte[]{}));
                put(BByteString.of("private"), BInteger.of(1));
                put(BByteString.of("name"), BByteString.of("Название"));
                put(BByteString.of("length"), BInteger.of(1));
                put(BByteString.of("md5sum"), BByteString.of("abcdefghijklmnopqrstuvwxyz"));
                put(BByteString.of("files"), BList.of(BDictionary.of(
                        BByteString.of("length"), BInteger.of(1),
                        BByteString.of("md5sum"), BByteString.of("abcdefghijklmnopqrstuvwxyz"),
                        BByteString.of("path"), BList.of(
                                BByteString.of("Название-1"),
                                BByteString.of("Название-2"),
                                BByteString.of("Название-3")))
                ));
            }}));
            put(BByteString.of("announce"), BByteString.of("announce"));
            put(BByteString.of("announce-list"), BList.of(BList.of(
                    BByteString.of("announce-list-item-1"),
                    BByteString.of("announce-list-item-2"))));
            put(BByteString.of("creation date"), BInteger.of(OffsetDateTime.now().toEpochSecond()));
            put(BByteString.of("comment"), BByteString.of("Комментарий"));
            put(BByteString.of("created by"), BByteString.of("Пользователь"));
            put(BByteString.of("encoding"), BByteString.of("utf8"));
        }});
        var mapper = new BObjectMapper();
        var parsed = mapper.readFromBDictionary(value, MetaInfo.class);
        var mapped = mapper.writeToBDictionary(parsed);
        assertEquals(value, mapped);
    }
}