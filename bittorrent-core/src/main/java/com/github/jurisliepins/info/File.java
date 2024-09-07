package com.github.jurisliepins.info;

import lombok.NonNull;

public record File(
        long length,
        String md5sum,
        String @NonNull [] path
) { }
