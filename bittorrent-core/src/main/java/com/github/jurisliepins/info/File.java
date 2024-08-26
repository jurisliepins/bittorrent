package com.github.jurisliepins.info;

import java.util.Objects;

public record File(
        long length,
        String md5sum,
        String[] path
) {
    public File {
        Objects.requireNonNull(path, "path is null");
    }
}
