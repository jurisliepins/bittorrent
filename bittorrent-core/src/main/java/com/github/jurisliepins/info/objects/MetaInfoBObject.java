package com.github.jurisliepins.info.objects;

import com.github.jurisliepins.BProperty;
import lombok.NonNull;

import java.time.OffsetDateTime;

public record MetaInfoBObject(
        @BProperty("info") @NonNull InfoBObject info,
        @BProperty("announce") @NonNull String announce,
        @BProperty("announce-list") String[][] announceList,
        @BProperty("creation date") OffsetDateTime creationDate,
        @BProperty("comment") String comment,
        @BProperty("created by") String createdBy,
        @BProperty("encoding") String encoding
) { }
