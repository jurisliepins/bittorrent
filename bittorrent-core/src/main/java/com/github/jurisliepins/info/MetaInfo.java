package com.github.jurisliepins.info;

import lombok.NonNull;

import java.time.OffsetDateTime;

public record MetaInfo(
        @NonNull Info info,
        @NonNull String announce,
        String[][] announceList,
        OffsetDateTime creationDate,
        String comment,
        String createdBy,
        String encoding
) { }
