package com.github.jurisliepins.info.entity;

import com.github.jurisliepins.BProperty;

import java.util.List;
import java.util.Objects;

public record FileEntity(
        @BProperty("length") long length,
        @BProperty("md5sum") String md5sum,
        @BProperty("path") List<String> path
) {
    public FileEntity {
        Objects.requireNonNull(path, "path is null");
    }
}
