package com.github.jurisliepins.info;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Meta-info tests")
public class MetaInfoTests {

    private static final String SINGLE_FILE_TORRENT = "single_file.torrent";

    private static final String MULTI_FILE_TORRENT = "multi_file.torrent";

    private static final InfoHash SINGLE_FILE_INFO_HASH = new InfoHash("aa171ca77f14f55d6ec23d7e9541b7791e6c383c");

    private static final InfoHash MULTI_FILE_INFO_HASH = new InfoHash("6e540ebbc92131138746231ff3e44f165fd3b373");

    @Test
    @DisplayName("Should decode single file meta-info")
    @SuppressWarnings("checkstyle:MagicNumber")
    public void shouldDecodeSingleFileMetaInfo() throws IOException {
        switch (MetaInfo.fromBytes(readFileAsBytes(SINGLE_FILE_TORRENT))) {
            case MetaInfo(
                    Info(int pieceLength,
                         byte[] pieces,
                         Boolean isPrivate,
                         String name,
                         Long length,
                         String md5sum,
                         List<File> files,
                         InfoHash hash),
                    String announce,
                    List<List<String>> announceList,
                    OffsetDateTime creationDate,
                    String comment,
                    String createdBy,
                    String encoding) -> {
                assertEquals(32768, pieceLength);
                assertNotNull(pieces);
                assertFalse(isPrivate);
                assertEquals("war_and_peace.txt", name);
                assertEquals(3266164, length);
                assertNull(md5sum);
                assertNull(files);
                assertEquals(SINGLE_FILE_INFO_HASH, hash);
                assertEquals("udp://tracker.openbittorrent.com:6969", announce);
                assertNull(announceList);
                assertEquals(OffsetDateTime.parse("2022-02-20T14:46:49Z"), creationDate);
                assertEquals("Single file torrent", comment);
                assertEquals("Juris Liepins", createdBy);
                assertEquals("UTF-8", encoding);
            }
            default -> throw new RuntimeException("Should not have reached this code");
        }
    }

    @Test
    @DisplayName("Should decode multi file meta-info")
    @SuppressWarnings("checkstyle:MagicNumber")
    public void shouldDecodeMultiFileMetaInfo() throws IOException {
        switch (MetaInfo.fromBytes(readFileAsBytes(MULTI_FILE_TORRENT))) {
            case MetaInfo(
                    Info(int pieceLength,
                         byte[] pieces,
                         Boolean isPrivate,
                         String name,
                         Long length,
                         String md5sum,
                         List<File> files,
                         InfoHash hash),
                    String announce,
                    List<List<String>> announceList,
                    OffsetDateTime creationDate,
                    String comment,
                    String createdBy,
                    String encoding) -> {
                assertEquals(32768, pieceLength);
                assertNotNull(pieces);
                assertFalse(isPrivate);
                assertEquals("war_and_peace", name);
                assertNull(length);
                assertNull(md5sum);
                assertEquals(4, files.size());
                assertEquals(11682, files.get(0).length());
                assertNull(files.get(0).md5sum());
                assertEquals(List.of("file_1.txt"), files.get(0).path());
                assertEquals(7923, files.get(1).length());
                assertNull(files.get(1).md5sum());
                assertEquals(List.of("file_2.txt"), files.get(1).path());
                assertEquals(8751, files.get(2).length());
                assertNull(files.get(2).md5sum());
                assertEquals(List.of("file_3.txt"), files.get(2).path());
                assertEquals(8180, files.get(3).length());
                assertNull(files.get(3).md5sum());
                assertEquals(List.of("file_4.txt"), files.get(3).path());
                assertEquals(MULTI_FILE_INFO_HASH, hash);
                assertEquals("udp://tracker.openbittorrent.com:6969", announce);
                assertNull(announceList);
                assertEquals(OffsetDateTime.parse("2022-02-20T16:04:09Z"), creationDate);
                assertEquals("Multi file torrent", comment);
                assertEquals("Juris Liepins", createdBy);
                assertEquals("UTF-8", encoding);
            }
            default -> throw new RuntimeException("Should not have reached this code");
        }
    }

    @Test
    @DisplayName("Should encode/decode UTF-8 strings")
    @SuppressWarnings("checkstyle:MagicNumber")
    public void shouldEncodeDecodeUtf8Strings() {
        final MetaInfo metaInfo = new MetaInfo(
                new Info(0,
                         new byte[] {},
                         false,
                         "Название",
                         0L,
                         null,
                         null,
                         null),
                "",
                null,
                OffsetDateTime.parse("2000-01-01T00:00:00Z"),
                "Комментарий",
                "Пользователь",
                null);
        switch (MetaInfo.fromBytes(metaInfo.toBytes())) {
            case MetaInfo(
                    Info(int pieceLength,
                         byte[] pieces,
                         Boolean isPrivate,
                         String name,
                         Long length,
                         String md5sum,
                         List<File> files,
                         InfoHash hash),
                    String announce,
                    List<List<String>> announceList,
                    OffsetDateTime creationDate,
                    String comment,
                    String createdBy,
                    String encoding) -> {
                assertEquals(0, pieceLength);
                assertNotNull(pieces);
                assertFalse(isPrivate);
                assertEquals("Название", name);
                assertEquals(0L, length);
                assertNull(md5sum);
                assertNull(files);
                assertEquals(new InfoHash("779e8f96663028f7654364721377d283bc80ea61"), hash);
                assertEquals("", announce);
                assertNull(announceList);
                assertEquals(OffsetDateTime.parse("2000-01-01T00:00:00Z"), creationDate);
                assertEquals("Комментарий", comment);
                assertEquals("Пользователь", createdBy);
                assertNull(encoding);
            }
            default -> throw new RuntimeException("Should not have reached this code");
        }
    }

    private byte[] readFileAsBytes(final String name) throws IOException {
        return Objects.requireNonNull(getClass()
                                              .getClassLoader()
                                              .getResourceAsStream(name))
                .readAllBytes();
    }

}
