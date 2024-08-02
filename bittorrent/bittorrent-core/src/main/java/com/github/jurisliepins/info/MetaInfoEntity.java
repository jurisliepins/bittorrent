package com.github.jurisliepins.info;

import com.github.jurisliepins.BConstants;
import com.github.jurisliepins.BObjectMapper;
import com.github.jurisliepins.BProperty;
import com.github.jurisliepins.stream.BOutputStream;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

public record MetaInfoEntity(
        @BProperty("info") InfoEntity info,
        @BProperty("announce") String announce,
        @BProperty("announce-list") List<List<String>> announceList,
        @BProperty("creation date") OffsetDateTime creationDate,
        @BProperty("comment") String comment,
        @BProperty("created by") String createdBy,
        @BProperty("encoding") String encoding
) {
    public MetaInfoEntity {
        Objects.requireNonNull(info, "info is null");
        Objects.requireNonNull(announce, "announce is null");
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
}
