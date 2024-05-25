package com.github.jurisliepins.info;

import com.github.jurisliepins.BProperty;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

public record MetaInfo(
        @BProperty("info") Info info,
        @BProperty("announce") String announce,
        @BProperty("announce-list") List<List<String>> announceList,
        @BProperty("creation date") OffsetDateTime creationDate,
        @BProperty("comment") String comment,
        @BProperty("created by") String createdBy,
        @BProperty("encoding") String encoding
) {
    public MetaInfo {
        Objects.requireNonNull(info, "info is null");
        Objects.requireNonNull(announce, "announce is null");
        Objects.requireNonNull(announceList, "announceList is null");
        Objects.requireNonNull(creationDate, "creationDate is null");
        Objects.requireNonNull(comment, "comment is null");
        Objects.requireNonNull(createdBy, "createdBy is null");
        Objects.requireNonNull(encoding, "encoding is null");
    }
}
