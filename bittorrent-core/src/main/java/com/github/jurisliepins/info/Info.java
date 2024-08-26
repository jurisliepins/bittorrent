package com.github.jurisliepins.info;

import com.github.jurisliepins.CoreException;

import java.util.Arrays;
import java.util.Objects;

public sealed interface Info permits Info.OneFileInfo, Info.ManyFileInfo {

    record OneFileInfo(
            int pieceLength,
            byte[] pieces,
            Boolean isPrivate,
            String name,
            Long length,
            String md5sum,
            InfoHash hash
    ) implements Info {
        public OneFileInfo {
            Objects.requireNonNull(pieces, "pieces is null");
            Objects.requireNonNull(name, "name is null");
        }
    }

    record ManyFileInfo(
            int pieceLength,
            byte[] pieces,
            Boolean isPrivate,
            String name,
            File[] files,
            InfoHash hash
    ) implements Info {
        public ManyFileInfo {
            Objects.requireNonNull(pieces, "pieces is null");
            Objects.requireNonNull(name, "name is null");
        }
    }

    default int pieceLength() {
        return switch (this) {
            case Info.OneFileInfo info -> info.pieceLength();
            case Info.ManyFileInfo info -> info.pieceLength();
        };
    }

    default byte[] pieces() {
        return switch (this) {
            case Info.OneFileInfo info -> info.pieces();
            case Info.ManyFileInfo info -> info.pieces();
        };
    }

    default Boolean isPrivate() {
        return switch (this) {
            case Info.OneFileInfo info -> info.isPrivate();
            case Info.ManyFileInfo info -> info.isPrivate();
        };
    }

    default String name() {
        return switch (this) {
            case Info.OneFileInfo info -> info.name();
            case Info.ManyFileInfo info -> info.name();
        };
    }

    default Long length() {
        return switch (this) {
            case Info.OneFileInfo info -> info.length();
            case Info.ManyFileInfo info -> Arrays.stream(info.files()).mapToLong(File::length).sum();
        };
    }

    default String md5sum() {
        return switch (this) {
            case Info.OneFileInfo info -> info.md5sum();
            case Info.ManyFileInfo ignored -> throw new CoreException("Info doesn't have single md5 sum");
        };
    }

    default File[] files() {
        return switch (this) {
            case Info.OneFileInfo ignored -> throw new CoreException("Info doesn't have files");
            case Info.ManyFileInfo info -> info.files();
        };
    }

    default InfoHash hash() {
        return switch (this) {
            case Info.OneFileInfo info -> info.hash();
            case Info.ManyFileInfo info -> info.hash();
        };
    }
}
