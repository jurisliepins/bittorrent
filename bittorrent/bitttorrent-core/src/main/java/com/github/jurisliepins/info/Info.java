package com.github.jurisliepins.info;

import com.github.jurisliepins.BProperty;

import java.util.List;
import java.util.Objects;

public record Info(
        @BProperty("piece length") int pieceLength,
        @BProperty("pieces") byte[] pieces,
        @BProperty("private") Boolean isPrivate,
        @BProperty("name") String name,
        @BProperty("length") long length,
        @BProperty("md5sum") String md5sum,
        @BProperty("files") List<File> files
) {
    public Info {
        Objects.requireNonNull(pieces, "pieces is null");
        Objects.requireNonNull(name, "name is null");
    }
}