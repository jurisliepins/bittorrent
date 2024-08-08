package com.github.jurisliepins.info;

import com.github.jurisliepins.BConstants;
import com.github.jurisliepins.BObjectMapper;
import com.github.jurisliepins.CoreException;
import com.github.jurisliepins.info.entity.InfoEntity;
import com.github.jurisliepins.info.entity.MetaInfoEntity;
import com.github.jurisliepins.stream.BInputStream;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record MetaInfo(
        Info info,
        String announce,
        List<List<String>> announceList,
        OffsetDateTime creationDate,
        String comment,
        String createdBy,
        String encoding
) {
    public static MetaInfo fromStream(final BInputStream stream) {
        try {
            return convert(new BObjectMapper().readFromStream(stream, MetaInfoEntity.class));
        } catch (Exception e) {
            throw new CoreException("Failed to read meta-info from stream", e);
        }
    }

    public static MetaInfo fromBytes(final byte[] bytes) {
        try {
            return convert(new BObjectMapper().readFromBytes(bytes, MetaInfoEntity.class));
        } catch (Exception e) {
            throw new CoreException("Failed to read meta-info from bytes", e);
        }
    }

    public static MetaInfo fromString(final String string) {
        try {
            return convert(new BObjectMapper().readFromString(string, BConstants.DEFAULT_ENCODING, MetaInfoEntity.class));
        } catch (Exception e) {
            throw new CoreException("Failed to read meta-info from string", e);
        }
    }

    private static MetaInfo convert(final MetaInfoEntity metaInfo) {
        if (metaInfo.info().files() != null && !metaInfo.info().files().isEmpty()) {
            return new MetaInfo(
                    new Info.ManyFileInfo(
                            metaInfo.info().pieceLength(),
                            metaInfo.info().pieces(),
                            metaInfo.info().isPrivate(),
                            metaInfo.info().name(),
                            metaInfo.info().files().stream()
                                    .map(file -> new File(file.length(), file.md5sum(), file.path()))
                                    .collect(Collectors.toList()),
                            hash(metaInfo.info())),
                    metaInfo.announce(),
                    metaInfo.announceList(),
                    metaInfo.creationDate(),
                    metaInfo.comment(),
                    metaInfo.createdBy(),
                    metaInfo.encoding());
        }
        return new MetaInfo(
                new Info.OneFileInfo(
                        metaInfo.info().pieceLength(),
                        metaInfo.info().pieces(),
                        metaInfo.info().isPrivate(),
                        metaInfo.info().name(),
                        metaInfo.info().length(),
                        metaInfo.info().md5sum(),
                        hash(metaInfo.info())),
                metaInfo.announce(),
                metaInfo.announceList(),
                metaInfo.creationDate(),
                metaInfo.comment(),
                metaInfo.createdBy(),
                metaInfo.encoding());
    }

    private static InfoHash hash(final InfoEntity info) {
        try {
            return new InfoHash(MessageDigest.getInstance("SHA-1").digest(info.toBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new CoreException("Failed to generate hash", e);
        }
    }
}
