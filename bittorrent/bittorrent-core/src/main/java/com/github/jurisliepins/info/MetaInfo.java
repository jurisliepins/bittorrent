package com.github.jurisliepins.info;

import com.github.jurisliepins.BConstants;
import com.github.jurisliepins.BObjectMapper;
import com.github.jurisliepins.BProperty;
import com.github.jurisliepins.stream.BInputStream;
import com.github.jurisliepins.stream.BOutputStream;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

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

    public static MetaInfo fromStream(final BInputStream stream) {
        try {
            return new BObjectMapper()
                    .readFromStream(stream, MetaInfo.class)
                    .withInfoHash();
        } catch (Exception e) {
            throw new InfoException("Failed to read meta-info from stream", e);
        }
    }

    public static MetaInfo fromBytes(final byte[] bytes) {
        try {
            return new BObjectMapper()
                    .readFromBytes(bytes, MetaInfo.class)
                    .withInfoHash();
        } catch (Exception e) {
            throw new InfoException("Failed to read meta-info from bytes", e);
        }
    }

    public static MetaInfo fromString(final String string) {
        try {
            return new BObjectMapper()
                    .readFromString(string, BConstants.DEFAULT_ENCODING, MetaInfo.class)
                    .withInfoHash();
        } catch (Exception e) {
            throw new InfoException("Failed to read meta-info from string", e);
        }
    }

    public BOutputStream toStream() {
        try {
            return new BObjectMapper().writeToStream(this);
        } catch (IOException e) {
            throw new InfoException("Failed to write info to stream", e);
        }
    }

    public byte[] toBytes() {
        try {
            return new BObjectMapper().writeToBytes(this);
        } catch (IOException e) {
            throw new InfoException("Failed to write info to bytes", e);
        }
    }

    public String toString() {
        try {
            return new BObjectMapper().writeToString(this, BConstants.DEFAULT_ENCODING);
        } catch (IOException e) {
            throw new InfoException("Failed to write info to string", e);
        }
    }

    private MetaInfo withInfoHash() throws NoSuchAlgorithmException {
        return new MetaInfo(
                new Info(info.pieceLength(),
                         info.pieces(),
                         info.isPrivate(),
                         info.name(),
                         info.length(),
                         info.md5sum(),
                         info.files(),
                         new InfoHash(MessageDigest.getInstance("SHA-1").digest(info.toBytes()))),
                announce,
                announceList,
                creationDate,
                comment,
                createdBy,
                encoding);
    }
}
