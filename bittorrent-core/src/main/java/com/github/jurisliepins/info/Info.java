package com.github.jurisliepins.info;

import com.github.jurisliepins.CoreException;
import lombok.NonNull;

import java.util.Arrays;

public sealed interface Info permits Info.UniFileInfo, Info.MultiFileInfo {

    record UniFileInfo(
            int pieceLength,
            byte @NonNull [] pieces,
            Boolean isPrivate,
            @NonNull String name,
            Long length,
            String md5sum,
            Hash hash
    ) implements Info { }

    record MultiFileInfo(
            int pieceLength,
            byte @NonNull [] pieces,
            Boolean isPrivate,
            @NonNull String name,
            File[] files,
            Hash hash
    ) implements Info { }

    default int pieceLength() {
        return switch (this) {
            case UniFileInfo info -> info.pieceLength();
            case MultiFileInfo info -> info.pieceLength();
        };
    }

    default byte[] pieces() {
        return switch (this) {
            case UniFileInfo info -> info.pieces();
            case MultiFileInfo info -> info.pieces();
        };
    }

    default Boolean isPrivate() {
        return switch (this) {
            case UniFileInfo info -> info.isPrivate();
            case MultiFileInfo info -> info.isPrivate();
        };
    }

    default String name() {
        return switch (this) {
            case UniFileInfo info -> info.name();
            case MultiFileInfo info -> info.name();
        };
    }

    default Long length() {
        return switch (this) {
            case UniFileInfo info -> info.length();
            case MultiFileInfo info -> Arrays.stream(info.files()).mapToLong(File::length).sum();
        };
    }

    default String md5sum() {
        return switch (this) {
            case UniFileInfo info -> info.md5sum();
            case MultiFileInfo ignored -> throw new CoreException("Info doesn't have single md5 sum");
        };
    }

    default File[] files() {
        return switch (this) {
            case UniFileInfo ignored -> throw new CoreException("Info doesn't have files");
            case MultiFileInfo info -> info.files();
        };
    }

    default Hash hash() {
        return switch (this) {
            case UniFileInfo info -> info.hash();
            case MultiFileInfo info -> info.hash();
        };
    }
}
