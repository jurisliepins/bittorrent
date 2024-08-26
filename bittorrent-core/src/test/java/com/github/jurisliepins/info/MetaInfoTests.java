package com.github.jurisliepins.info;

import com.github.jurisliepins.info.objects.InfoBObject;
import com.github.jurisliepins.info.objects.MetaInfoBObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Meta-info tests")
public class MetaInfoTests {

    private static final String ONE_FILE_TORRENT = "one_file.torrent";

    private static final String MANY_FILE_TORRENT = "many_file.torrent";

    private static final InfoHash ONE_FILE_INFO_HASH = new InfoHash("aa171ca77f14f55d6ec23d7e9541b7791e6c383c");

    private static final InfoHash MANY_FILE_INFO_HASH = new InfoHash("6e540ebbc92131138746231ff3e44f165fd3b373");

    @Test
    @DisplayName("Should decode one file meta-info")
    public void shouldDecodeOneFileMetaInfo() throws IOException {
        switch (MetaInfo.fromBytes(readFileAsBytes(ONE_FILE_TORRENT))) {
            case MetaInfo metaInfo -> {
                assertNotNull(metaInfo.info());
                assertEquals("udp://tracker.openbittorrent.com:6969", metaInfo.announce());
                assertNull(metaInfo.announceList());
                assertEquals(OffsetDateTime.parse("2022-02-20T14:46:49Z"), metaInfo.creationDate());
                assertEquals("Single file torrent", metaInfo.comment());
                assertEquals("Juris Liepins", metaInfo.createdBy());
                assertEquals("UTF-8", metaInfo.encoding());
                switch (metaInfo.info()) {
                    case Info.OneFileInfo info -> {
                        assertEquals(32768, info.pieceLength());
                        assertNotNull(info.pieces());
                        assertFalse(info.isPrivate());
                        assertEquals("war_and_peace.txt", info.name());
                        assertEquals(3266164, info.length());
                        assertNull(info.md5sum());
                        assertEquals(ONE_FILE_INFO_HASH, info.hash());
                    }
                    default -> throw new RuntimeException("Should not have reached this code");
                }
            }
        }
    }

    @Test
    @DisplayName("Should decode many file meta-info")
    public void shouldDecodeManyFileMetaInfo() throws IOException {
        switch (MetaInfo.fromBytes(readFileAsBytes(MANY_FILE_TORRENT))) {
            case MetaInfo metaInfo -> {
                assertNotNull(metaInfo.info());
                assertEquals("udp://tracker.openbittorrent.com:6969", metaInfo.announce());
                assertNull(metaInfo.announceList());
                assertEquals(OffsetDateTime.parse("2022-02-20T16:04:09Z"), metaInfo.creationDate());
                assertEquals("Multi file torrent", metaInfo.comment());
                assertEquals("Juris Liepins", metaInfo.createdBy());
                assertEquals("UTF-8", metaInfo.encoding());
                switch (metaInfo.info()) {
                    case Info.ManyFileInfo info -> {
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
                        assertEquals(MANY_FILE_INFO_HASH, info.hash());
                    }
                    default -> throw new RuntimeException("Should not have reached this code");
                }
            }
        }
    }

    @Test
    @DisplayName("Should encode/decode UTF-8 strings")
    public void shouldEncodeDecodeUtf8Strings() {
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
                OffsetDateTime.parse("2000-01-01T00:00:00Z").toEpochSecond(),
                "Комментарий",
                "Пользователь",
                null);
        switch (MetaInfo.fromBytes(utf8MetaInfo.toBytes())) {
            case MetaInfo metaInfo -> {
                assertEquals("", metaInfo.announce());
                assertNull(metaInfo.announceList());
                assertEquals(OffsetDateTime.parse("2000-01-01T00:00:00Z"), metaInfo.creationDate());
                assertEquals("Комментарий", metaInfo.comment());
                assertEquals("Пользователь", metaInfo.createdBy());
                assertNull(metaInfo.encoding());
                switch (metaInfo.info()) {
                    case Info.OneFileInfo info -> {
                        assertEquals(0, info.pieceLength());
                        assertNotNull(info.pieces());
                        assertFalse(info.isPrivate());
                        assertEquals("Название", info.name());
                        assertEquals(0L, info.length());
                        assertNull(info.md5sum());
                        assertEquals(new InfoHash("779e8f96663028f7654364721377d283bc80ea61"), info.hash());
                    }
                    default -> throw new RuntimeException("Should not have reached this code");
                }
            }
        }
    }

    private byte[] readFileAsBytes(final String name) throws IOException {
        return Objects.requireNonNull(getClass()
                                              .getClassLoader()
                                              .getResourceAsStream(name))
                .readAllBytes();
    }

}
