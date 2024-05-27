package com.github.jurisliepins.info;

import com.github.jurisliepins.BProperty;

import java.util.List;
import java.util.Objects;

public record File(
        @BProperty("length") long length,
        @BProperty("md5sum") String md5sum,
        @BProperty("path") List<String> path
) {
    public File {
        Objects.requireNonNull(path, "path is null");
    }
}
