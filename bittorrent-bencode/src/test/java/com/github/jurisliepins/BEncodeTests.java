package com.github.jurisliepins;

import com.github.jurisliepins.value.BValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.jurisliepins.value.BByteString.bstr;
import static com.github.jurisliepins.value.BDictionary.bdict;
import static com.github.jurisliepins.value.BInteger.bint;
import static com.github.jurisliepins.value.BList.blist;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("BEncode/BDecode tests")
public final class BEncodeTests {

    @Test
    @DisplayName("Should throw when decoding corrupt data")
    public void shouldThrowWhenDecodingCorruptData() {
        assertThrows(BException.class, () -> BDecoder.fromString("corruption!", StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Should throw when decoding corrupt string")
    public void shouldThrowWhenDecodingCorruptString() {
        assertThrows(BException.class, () -> BDecoder.fromString("50:i'm too short", StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Should throw when decoding corrupt integer")
    public void shouldThrowWhenDecodingCorruptInteger() {
        assertThrows(BException.class, () -> BDecoder.fromString("i9223372036854775807", StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Should throw when decoding corrupt list")
    public void shouldThrowWhenDecodingCorruptList() {
        assertThrows(BException.class, () -> BDecoder.fromString("l3:3521:a3:ae", StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Should throw when decoding corrupt dictionary")
    public void shouldThrowWhenDecodingCorruptDictionary() {
        assertThrows(BException.class, () -> BDecoder.fromString("d3:3521:a3:aedddd", StandardCharsets.UTF_8));
    }

    private static void shouldDecodeEncode(final String value) throws IOException {
        var decoded = BDecoder.fromString(value, StandardCharsets.UTF_8);
        var encoded = BEncoder.toString(decoded, StandardCharsets.UTF_8);
        assertEquals(value, encoded);
    }

    private static void shouldEncodeDecode(final BValue value) throws IOException {
        var encoded = BEncoder.toString(value, StandardCharsets.UTF_8);
        var decoded = BDecoder.fromString(encoded, StandardCharsets.UTF_8);
        assertEquals(value, decoded);
    }

    @Test
    @DisplayName("Should decode/encode string")
    public void shouldDecodeEncodeString() throws IOException {
        var value = "22:this is my test string";
        shouldDecodeEncode(value);
    }

    @Test
    @DisplayName("Should decode/encode UTF-8 string")
    public void shouldDecodeEncodeUtf8String() throws IOException {
        var value = "17:ɄɅ ɱ ϴ ЂЃЃ";
        shouldDecodeEncode(value);
    }

    @Test
    @DisplayName("Should decode/encode empty string")
    public void shouldDecodeEncodeEmptyString() throws IOException {
        var value = "0:";
        shouldDecodeEncode(value);
    }

    @Test
    @DisplayName("Should encode/decode string")
    public void shouldEncodeDecodeString() throws IOException {
        var value = bstr("22:this is my test string");
        shouldEncodeDecode(value);
    }

    @Test
    @DisplayName("Should encode/decode UTF-8 string")
    public void shouldEncodeDecodeUtf8String() throws IOException {
        var value = bstr("17:ɄɅ ɱ ϴ ЂЃЃ");
        shouldEncodeDecode(value);
    }

    @Test
    @DisplayName("Should encode/decode empty string")
    public void shouldEncodeDecodeEmptyString() throws IOException {
        var value = bstr("0:");
        shouldEncodeDecode(value);
    }


    @Test
    @DisplayName("Should decode/encode integer")
    public void shouldDecodeEncodeInteger() throws IOException {
        var value = "i12412e";
        shouldDecodeEncode(value);
    }

    @Test
    @DisplayName("Should decode/encode negative integer")
    public void shouldDecodeEncodeNegativeInteger() throws IOException {
        var value = "i-12412e";
        shouldDecodeEncode(value);
    }

    @Test
    @DisplayName("Should decode/encode zero integer")
    public void shouldDecodeEncodeZeroInteger() throws IOException {
        var value = "i0e";
        shouldDecodeEncode(value);
    }

    @Test
    @DisplayName("Should encode/decode integer")
    public void shouldEncodeDecodeInteger() throws IOException {
        var value = bint(12412L);
        shouldEncodeDecode(value);
    }

    @Test
    @DisplayName("Should encode/decode negative integer")
    public void shouldEncodeDecodeNegativeInteger() throws IOException {
        var value = bint(-12412L);
        shouldEncodeDecode(value);
    }

    @Test
    @DisplayName("Should encode/decode zero integer")
    public void shouldEncodeDecodeZeroInteger() throws IOException {
        var value = bint(0L);
        shouldEncodeDecode(value);
    }


    @Test
    @DisplayName("Should decode/encode list")
    public void shouldDecodeEncodeList() throws IOException {
        var value = "l4:test5:tests6:testede";
        shouldDecodeEncode(value);
    }

    @Test
    @DisplayName("Should decode/encode empty list")
    public void shouldDecodeEncodeEmptyList() throws IOException {
        var value = "le";
        shouldDecodeEncode(value);
    }

    @Test
    @DisplayName("Should decode/encode stacked list")
    public void shouldDecodeEncodeStackedList() throws IOException {
        var value = "l6:stringl7:stringsl8:stringedei23456eei12345ee";
        shouldDecodeEncode(value);
    }

    @Test
    @DisplayName("Should encode/decode list")
    public void shouldEncodeDecodeList() throws IOException {
        var value = blist(
                bstr("test"),
                bstr("tests"),
                bstr("tested"));
        shouldEncodeDecode(value);
    }

    @Test
    @DisplayName("Should encode/decode empty list")
    public void shouldEncodeDecodeEmptyList() throws IOException {
        var value = blist();
        shouldEncodeDecode(value);
    }

    @Test
    @DisplayName("Should encode/decode stacked list")
    public void shouldEncodeDecodeStackedList() throws IOException {
        var value = blist(
                bstr("string"),
                blist(
                        bstr("strings"),
                        blist(bstr("stringed")),
                        bint(23456L)),
                bint(12345L));
        shouldEncodeDecode(value);
    }

    @Test
    @DisplayName("Should decode/encode dictionary")
    public void shouldDecodeEncodeDictionary() throws IOException {
        var value = "d4:spaml1:a1:bee";
        shouldDecodeEncode(value);
    }

    @Test
    @DisplayName("Should decode/encode empty dictionary")
    public void shouldDecodeEncodeEmptyDictionary() throws IOException {
        var value = "de";
        shouldDecodeEncode(value);
    }

    @Test
    @DisplayName("Should decode/encode stacked dictionary")
    public void shouldDecodeEncodeStackedDictionary() throws IOException {
        var value = "d4:testd5:testsli12345ei12345ee2:tod3:tomi12345eeee";
        shouldDecodeEncode(value);
    }

    @Test
    @DisplayName("Should decode/encode sorted dictionary")
    public void shouldDecodeEncodeSortedDictionary() throws IOException {
        var value = "d1:c1:c1:b1:b1:a1:ae";
        var decoded = BDecoder.fromString(value, StandardCharsets.UTF_8);
        var encoded = BEncoder.toString(decoded, StandardCharsets.UTF_8);
        assertEquals("d1:a1:a1:b1:b1:c1:ce", encoded);
    }

    @Test
    @DisplayName("Should encode/decode dictionary")
    public void shouldEncodeDecodeDictionary() throws IOException {
        var value = bdict(
                bstr("spam"), blist(
                        bstr("a"),
                        bstr("b")));
        shouldEncodeDecode(value);
    }

    @Test
    @DisplayName("Should encode/decode empty dictionary")
    public void shouldEncodeDecodeEmptyDictionary() throws IOException {
        var value = bdict();
        shouldEncodeDecode(value);
    }

    @Test
    @DisplayName("Should encode/decode dictionary")
    public void shouldEncodeDecodeStackedDictionary() throws IOException {
        var value = bdict(
                bstr("test"), bdict(
                        bstr("to"), bdict(bstr("tom"), bint(12345L)),
                        bstr("tests"), blist(bint(12345L), bint(12345L))));
        shouldEncodeDecode(value);
    }

    @Test
    @DisplayName("Should encode/decode sorted dictionary")
    public void shouldEncodeDecodeSortedDictionary() throws IOException {
        var value = bdict(
                bstr("c"), bstr("c"),
                bstr("b"), bstr("b"),
                bstr("a"), bstr("a"));
        var encoded = BEncoder.toString(value, StandardCharsets.UTF_8);
        var decoded = BDecoder.fromString(encoded, StandardCharsets.UTF_8);
        assertEquals(value, decoded);
    }
}
