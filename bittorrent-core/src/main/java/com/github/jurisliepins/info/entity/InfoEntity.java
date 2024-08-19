package com.github.jurisliepins.info.entity;

import com.github.jurisliepins.BConstants;
import com.github.jurisliepins.BObjectMapper;
import com.github.jurisliepins.BProperty;
import com.github.jurisliepins.CoreException;
import com.github.jurisliepins.stream.BOutputStream;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public record InfoEntity(
        @BProperty("piece length") int pieceLength,
        @BProperty("pieces") byte[] pieces,
        @BProperty("private") Boolean isPrivate,
        @BProperty("name") String name,
        @BProperty("length") Long length,
        @BProperty("md5sum") String md5sum,
        @BProperty("files") List<FileEntity> files
) {
    public InfoEntity {
        Objects.requireNonNull(pieces, "pieces is null");
        Objects.requireNonNull(name, "name is null");
    }

    public BOutputStream toStream() {
        try {
            return new BObjectMapper().writeToStream(this);
        } catch (IOException e) {
            throw new CoreException("Failed to write info to stream", e);
        }
    }

    public byte[] toBytes() {
        try {
            return new BObjectMapper().writeToBytes(this);
        } catch (IOException e) {
            throw new CoreException("Failed to write info to bytes", e);
        }
    }

    public String toString() {
        try {
            return new BObjectMapper().writeToString(this, BConstants.DEFAULT_ENCODING);
        } catch (IOException e) {
            throw new CoreException("Failed to write info to string", e);
        }
    }
}
