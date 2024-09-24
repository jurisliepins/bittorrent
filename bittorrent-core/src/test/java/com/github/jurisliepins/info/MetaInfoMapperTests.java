package com.github.jurisliepins.info;

import com.github.jurisliepins.BObjectMapper;
import com.github.jurisliepins.info.objects.InfoBObject;
import com.github.jurisliepins.info.objects.MetaInfoBObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.OffsetDateTime;

import static com.github.jurisliepins.utils.InfoUtils.readMultiFileTorrent;
import static com.github.jurisliepins.utils.InfoUtils.readUniFileTorrent;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Meta-info mapper tests")
public class MetaInfoMapperTests {

    private static final Hash UNI_FILE_INFO_HASH = new Hash("aa171ca77f14f55d6ec23d7e9541b7791e6c383c");
    private static final Hash MULTI_FILE_INFO_HASH = new Hash("6e540ebbc92131138746231ff3e44f165fd3b373");

    @Test
    @DisplayName("Should decode uni file meta-info")
    public void shouldDecodeUniFileMetaInfo() throws IOException {
        var mi = MetaInfoMapper.fromBytes(readUniFileTorrent());
        assertNotNull(mi.info());
        assertEquals("udp://tracker.openbittorrent.com:6969", mi.announce());
        assertNull(mi.announceList());
        assertEquals(OffsetDateTime.parse("2022-02-20T14:46:49Z"), mi.creationDate());
        assertEquals("Single file torrent", mi.comment());
        assertEquals("Juris Liepins", mi.createdBy());
        assertEquals("UTF-8", mi.encoding());
        switch (mi.info()) {
            case Info.UniFileInfo info -> {
                assertEquals(32768, info.pieceLength());
                assertNotNull(info.pieces());
                assertFalse(info.isPrivate());
                assertEquals("war_and_peace.txt", info.name());
                assertEquals(3266164, info.length());
                assertNull(info.md5sum());
                assertEquals(UNI_FILE_INFO_HASH, info.hash());
            }
            default -> throw new RuntimeException("Should have decoded uni-file info");
        }
    }

    @Test
    @DisplayName("Should decode multi file meta-info")
    public void shouldDecodeMultiFileMetaInfo() throws IOException {
        var mi = MetaInfoMapper.fromBytes(readMultiFileTorrent());
        assertNotNull(mi.info());
        assertEquals("udp://tracker.openbittorrent.com:6969", mi.announce());
        assertNull(mi.announceList());
        assertEquals(OffsetDateTime.parse("2022-02-20T16:04:09Z"), mi.creationDate());
        assertEquals("Multi file torrent", mi.comment());
        assertEquals("Juris Liepins", mi.createdBy());
        assertEquals("UTF-8", mi.encoding());
        switch (mi.info()) {
            case Info.MultiFileInfo info -> {
                assertEquals(32768, info.pieceLength());
                assertNotNull(info.pieces());
                assertFalse(info.isPrivate());
                assertEquals("war_and_peace", info.name());
                assertEquals(4, info.files().length);
                assertEquals(11682, info.files()[0].length());
                assertNull(info.files()[0].md5sum());
                assertArrayEquals(new String[]{"file_1.txt"}, info.files()[0].path());
                assertEquals(7923, info.files()[1].length());
                assertNull(info.files()[1].md5sum());
                assertArrayEquals(new String[]{"file_2.txt"}, info.files()[1].path());
                assertEquals(8751, info.files()[2].length());
                assertNull(info.files()[2].md5sum());
                assertArrayEquals(new String[]{"file_3.txt"}, info.files()[2].path());
                assertEquals(8180, info.files()[3].length());
                assertNull(info.files()[3].md5sum());
                assertArrayEquals(new String[]{"file_4.txt"}, info.files()[3].path());
                assertEquals(MULTI_FILE_INFO_HASH, info.hash());
            }
            default -> throw new RuntimeException("Should have decoded multi-file info");
        }
    }

    @Test
    @DisplayName("Should encode/decode UTF-8 strings")
    public void shouldEncodeDecodeUtf8Strings() throws IOException {
        final MetaInfoBObject utf8MetaInfo = new MetaInfoBObject(
                new InfoBObject(
                        0,
                        new byte[]{},
                        false,
                        "Название",
                        0L,
                        null,
                        null),
                "",
                null,
                OffsetDateTime.parse("2000-01-01T00:00:00Z"),
                "Комментарий",
                "Пользователь",
                null);
        var mi = MetaInfoMapper.fromBytes(BObjectMapper.toBytes(utf8MetaInfo));
        assertEquals("", mi.announce());
        assertNull(mi.announceList());
        assertEquals(OffsetDateTime.parse("2000-01-01T00:00:00Z"), mi.creationDate());
        assertEquals("Комментарий", mi.comment());
        assertEquals("Пользователь", mi.createdBy());
        assertNull(mi.encoding());
        switch (mi.info()) {
            case Info.UniFileInfo info -> {
                assertEquals(0, info.pieceLength());
                assertNotNull(info.pieces());
                assertFalse(info.isPrivate());
                assertEquals("Название", info.name());
                assertEquals(0L, info.length());
                assertNull(info.md5sum());
                assertEquals(new Hash("779e8f96663028f7654364721377d283bc80ea61"), info.hash());
            }
            default -> throw new RuntimeException("Should have decoded uni-file info");
        }
    }
}
