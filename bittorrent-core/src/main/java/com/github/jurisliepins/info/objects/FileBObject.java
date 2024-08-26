package com.github.jurisliepins.info.objects;

import com.github.jurisliepins.BProperty;

import java.util.Objects;

public record FileBObject(
        @BProperty("length") long length,
        @BProperty("md5sum") String md5sum,
        @BProperty("path") String[] path
) {
    public FileBObject {
        Objects.requireNonNull(path, "path is null");
    }
}
