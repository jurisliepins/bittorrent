package com.github.jurisliepins.piece;

import com.github.jurisliepins.info.MetaInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Piece tests")
public final class PieceTests {

    private static final String UNI_FILE_TORRENT = "uni_file.torrent";
    private static final String MULTI_FILE_TORRENT = "multi_file.torrent";

    @Test
    @DisplayName("Should create pieces from uni-file info")
    public void shouldCreatePiecesFromUniFileInfo() throws IOException {
        switch (MetaInfo.fromBytes(readFileAsBytes(UNI_FILE_TORRENT))) {
            case MetaInfo mi -> {
                var pieces = Piece.fromInfo(mi.info());
                assertEquals(100, pieces.length);
                // Piece 1.
                assertEquals(0, pieces[0].index());
                assertEquals(0, pieces[0].offset());
                assertEquals(32768, pieces[0].length());
                assertEquals(1, pieces[0].files().length);
                assertEquals("war_and_peace.txt", pieces[0].files()[0].path());
                assertEquals(0, pieces[1].files()[0].offset());
                assertEquals(3266164, pieces[1].files()[0].length());
                // Piece 100.
                assertEquals(99, pieces[99].index());
                assertEquals(3244032, pieces[99].offset());
                assertEquals(22132, pieces[99].length());
                assertEquals(1, pieces[99].files().length);
                assertEquals("war_and_peace.txt", pieces[99].files()[0].path());
                assertEquals(0, pieces[99].files()[0].offset());
                assertEquals(3266164, pieces[99].files()[0].length());
            }
        }
    }

    @Test
    @DisplayName("Should create pieces from multi-file info")
    public void shouldCreatePiecesFromMultiFileInfo() throws IOException {
        switch (MetaInfo.fromBytes(readFileAsBytes(MULTI_FILE_TORRENT))) {
            case MetaInfo mi -> {
                var pieces = Piece.fromInfo(mi.info());
                assertEquals(2, pieces.length);
                // Piece 1.
                assertEquals(0, pieces[0].index());
                assertEquals(0, pieces[0].offset());
                assertEquals(32768, pieces[0].length());
                assertEquals(4, pieces[0].files().length);
                assertEquals("war_and_peace/file_1.txt", pieces[0].files()[0].path());
                assertEquals(0, pieces[0].files()[0].offset());
                assertEquals(11682, pieces[0].files()[0].length());
                assertEquals("war_and_peace/file_2.txt", pieces[0].files()[1].path());
                assertEquals(11682, pieces[0].files()[1].offset());
                assertEquals(7923, pieces[0].files()[1].length());
                assertEquals("war_and_peace/file_3.txt", pieces[0].files()[2].path());
                assertEquals(19605, pieces[0].files()[2].offset());
                assertEquals(8751, pieces[0].files()[2].length());
                assertEquals("war_and_peace/file_4.txt", pieces[0].files()[3].path());
                assertEquals(28356, pieces[0].files()[3].offset());
                assertEquals(8180, pieces[0].files()[3].length());
                // Piece 2.
                assertEquals(1, pieces[1].index());
                assertEquals(32768, pieces[1].offset());
                assertEquals(3768, pieces[1].length());
                assertEquals(1, pieces[1].files().length);
                assertEquals("war_and_peace/file_4.txt", pieces[1].files()[0].path());
                assertEquals(28356, pieces[1].files()[0].offset());
                assertEquals(8180, pieces[1].files()[0].length());
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
