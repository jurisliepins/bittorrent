package com.github.jurisliepins.info;

import java.util.List;
import java.util.Objects;

public record File(
        long length,
        String md5sum,
        List<String> path
) {
    public File {
        Objects.requireNonNull(path, "path is null");
    }
}
