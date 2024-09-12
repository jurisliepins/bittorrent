package com.github.jurisliepins.piece;

import com.github.jurisliepins.info.Info;
import lombok.NonNull;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.IntStream;

public record Piece(
        int index,
        byte @NonNull [] hash,
        long offset,
        long length,
        Piece.File @NonNull [] files) {

    public record File(
            @NonNull String path,
            long offset,
            long length) { }

    public static Piece[] fromInfo(@NonNull final Info info) {
        return switch (info) {
            case Info.UniFileInfo ufi -> createPiecesFromUniFileInfo(ufi);
            case Info.MultiFileInfo mfi -> createPiecesFromMultiFileInfo(mfi);
        };
    }

    private static long computePieceOffset(final int index, final int pieceLength) {
        return (long) index * (long) pieceLength;
    }

    private static long computePieceLength(final int index, final int pieceLength, final long length) {
        var head = (long) index * (long) pieceLength;
        var tail = (long) index * (long) pieceLength + (long) pieceLength;
        // Last piece may be shorter than the given piece length, so we check if we're out of bounds of the entire length
        // of the torrent in tail. If we are, then we return that shorter length.
        if (tail > length) {
            return length - head;
        }
        return pieceLength;
    }

    private static Piece[] createPiecesFromUniFileInfo(@NonNull final Info.UniFileInfo info) {
        var files = new Piece.File[]{
                new Piece.File(info.name(), 0L, info.length())
        };
        return IntStream.range(0, info.pieces().length)
                .mapToObj(index -> new Piece(
                        index,
                        info.pieces()[index],
                        computePieceOffset(index, info.pieceLength()),
                        computePieceLength(index, info.pieceLength(), info.length()),
                        files))
                .toArray(Piece[]::new);
    }

    private static Piece[] createPiecesFromMultiFileInfo(@NonNull final Info.MultiFileInfo info) {
        // File offsets are based on preceding file lengths. The offset is calculated as if
        // all files have been concatenate into 1.
        var files = new Piece.File[info.files().length];
        for (var i = 0; i < info.files().length; i++) {
            if (i == 0) {
                files[i] = new Piece.File(
                        Paths.get(info.name(), info.files()[i].path()).toString(),
                        0L,
                        info.files()[i].length());
            } else {
                files[i] = new Piece.File(
                        Paths.get(info.name(), info.files()[i].path()).toString(),
                        files[i - 1].offset() + files[i - 1].length(),
                        info.files()[i].length());
            }
        }
        return IntStream.range(0, info.pieces().length)
                .mapToObj(index -> new Piece(
                        index,
                        info.pieces()[index],
                        computePieceOffset(index, info.pieceLength()),
                        computePieceLength(index, info.pieceLength(), info.length()),
                        Arrays.stream(files)
                                .filter(file -> {
                                    var fileHead = file.offset();
                                    var fileTail = fileHead + file.length();
                                    var pieceHead = computePieceOffset(index, info.pieceLength());
                                    var pieceTail = pieceHead + computePieceLength(index, info.pieceLength(), info.length());
                                    // We only keep files that this piece intersects with.
                                    return (fileHead <= pieceTail) && (fileTail >= pieceHead);
                                })
                                .toArray(Piece.File[]::new)))
                .toArray(Piece[]::new);
    }
}
