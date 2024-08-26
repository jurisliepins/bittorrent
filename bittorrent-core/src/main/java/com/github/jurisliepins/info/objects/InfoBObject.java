package com.github.jurisliepins.info.objects;

import com.github.jurisliepins.BConstants;
import com.github.jurisliepins.BObjectMapper;
import com.github.jurisliepins.BProperty;
import com.github.jurisliepins.CoreException;
import com.github.jurisliepins.stream.BOutputStream;

import java.io.IOException;
import java.util.Objects;

public record InfoBObject(
        @BProperty("piece length") int pieceLength,
        @BProperty("pieces") byte[] pieces,
        @BProperty("private") Boolean isPrivate,
        @BProperty("name") String name,
        @BProperty("length") Long length,
        @BProperty("md5sum") String md5sum,
        @BProperty("files") FileBObject[] files
) {
    public InfoBObject {
        Objects.requireNonNull(pieces, "pieces is null");
        Objects.requireNonNull(name, "name is null");
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
