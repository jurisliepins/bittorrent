package com.github.jurisliepins.info.objects;

import com.github.jurisliepins.BProperty;
import lombok.NonNull;

public record FileBObject(
        @BProperty("length") long length,
        @BProperty("md5sum") String md5sum,
        @BProperty("path") String @NonNull [] path
) { }
