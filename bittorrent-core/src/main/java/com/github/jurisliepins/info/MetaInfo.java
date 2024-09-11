package com.github.jurisliepins.info;

import com.github.jurisliepins.BConstants;
import com.github.jurisliepins.BObjectMapper;
import com.github.jurisliepins.CoreException;
import com.github.jurisliepins.info.objects.InfoBObject;
import com.github.jurisliepins.info.objects.MetaInfoBObject;
import com.github.jurisliepins.stream.BInputStream;
import lombok.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

public record MetaInfo(
        Info info,
        String announce,
        String[][] announceList,
        OffsetDateTime creationDate,
        String comment,
        String createdBy,
        String encoding
) {
    public static MetaInfo fromStream(@NonNull final BInputStream stream) {
        try {
            return convert(BObjectMapper.fromStream(stream, MetaInfoBObject.class));
        } catch (Exception e) {
            throw new CoreException("Failed to read meta-info from stream", e);
        }
    }

    public static MetaInfo fromBytes(final byte @NonNull [] bytes) {
        try {
            return convert(BObjectMapper.fromBytes(bytes, MetaInfoBObject.class));
        } catch (Exception e) {
            throw new CoreException("Failed to read meta-info from bytes", e);
        }
    }

    public static MetaInfo fromString(@NonNull final String string) {
        try {
            return convert(BObjectMapper.fromString(string, BConstants.DEFAULT_ENCODING, MetaInfoBObject.class));
        } catch (Exception e) {
            throw new CoreException("Failed to read meta-info from string", e);
        }
    }

    private static MetaInfo convert(@NonNull final MetaInfoBObject metaInfo) {
        if (metaInfo.info().files() != null && metaInfo.info().files().length > 0) {
            return new MetaInfo(
                    new Info.MultiFileInfo(
                            metaInfo.info().pieceLength(),
                            chunkPieces(metaInfo.info().pieces()),
                            metaInfo.info().isPrivate(),
                            metaInfo.info().name(),
                            Arrays.stream(metaInfo.info().files())
                                    .map(file -> new File(file.length(),
                                                          file.md5sum(),
                                                          file.path()))
                                    .toArray(File[]::new),
                            hash(metaInfo.info())),
                    metaInfo.announce(),
                    metaInfo.announceList(),
                    OffsetDateTime.ofInstant(Instant.ofEpochSecond(metaInfo.creationDate()), ZoneOffset.UTC),
                    metaInfo.comment(),
                    metaInfo.createdBy(),
                    metaInfo.encoding());
        }
        return new MetaInfo(
                new Info.UniFileInfo(
                        metaInfo.info().pieceLength(),
                        chunkPieces(metaInfo.info().pieces()),
                        metaInfo.info().isPrivate(),
                        metaInfo.info().name(),
                        metaInfo.info().length(),
                        metaInfo.info().md5sum(),
                        hash(metaInfo.info())),
                metaInfo.announce(),
                metaInfo.announceList(),
                OffsetDateTime.ofInstant(Instant.ofEpochSecond(metaInfo.creationDate()), ZoneOffset.UTC),
                metaInfo.comment(),
                metaInfo.createdBy(),
                metaInfo.encoding());
    }

    private static byte[][] chunkPieces(final byte @NonNull [] pieces) {
        var length = 20;
        var chunks = new byte[pieces.length / length][];
        for (int i = 0; i < chunks.length; i++) {
            chunks[i] = Arrays.copyOfRange(pieces, i * length, (i + 1) * length);
        }
        return chunks;
    }

    private static InfoHash hash(@NonNull final InfoBObject info) {
        try {
            return new InfoHash(MessageDigest.getInstance("SHA-1").digest(info.toBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new CoreException("Failed to generate hash", e);
        }
    }
}
