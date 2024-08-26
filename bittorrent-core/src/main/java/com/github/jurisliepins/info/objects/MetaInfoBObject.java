package com.github.jurisliepins.info.objects;

import com.github.jurisliepins.BConstants;
import com.github.jurisliepins.BObjectMapper;
import com.github.jurisliepins.BProperty;
import com.github.jurisliepins.CoreException;
import com.github.jurisliepins.stream.BOutputStream;

import java.io.IOException;
import java.util.Objects;

public record MetaInfoBObject(
        @BProperty("info") InfoBObject info,
        @BProperty("announce") String announce,
        @BProperty("announce-list") String[][] announceList,
        @BProperty("creation date") Long creationDate,
        @BProperty("comment") String comment,
        @BProperty("created by") String createdBy,
        @BProperty("encoding") String encoding
) {
    public MetaInfoBObject {
        Objects.requireNonNull(info, "info is null");
        Objects.requireNonNull(announce, "announce is null");
    }

    public BOutputStream toStream() {
        try {
            return BObjectMapper.toStream(this);
        } catch (IOException e) {
            throw new CoreException("Failed to write info to stream", e);
        }
    }

    public byte[] toBytes() {
        try {
            return BObjectMapper.toBytes(this);
        } catch (IOException e) {
            throw new CoreException("Failed to write info to bytes", e);
        }
    }

    public String toString() {
        try {
            return BObjectMapper.toString(this, BConstants.DEFAULT_ENCODING);
        } catch (IOException e) {
            throw new CoreException("Failed to write info to string", e);
        }
    }
}
