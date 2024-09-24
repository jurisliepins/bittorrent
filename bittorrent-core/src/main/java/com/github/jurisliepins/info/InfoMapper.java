package com.github.jurisliepins.info;

import com.github.jurisliepins.info.objects.InfoBObject;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Arrays;

@UtilityClass
public final class InfoMapper {
    public static Info fromObject(@NonNull final InfoBObject value) {
        if (value.files() != null && value.files().length > 0) {
            return new Info.MultiFileInfo(
                    value.pieceLength(),
                    value.pieces(),
                    value.isPrivate(),
                    value.name(),
                    Arrays.stream(value.files())
                            .map(FileMapper::fromObject)
                            .toArray(File[]::new),
                    value.hash());
        }
        return new Info.UniFileInfo(
                value.pieceLength(),
                value.pieces(),
                value.isPrivate(),
                value.name(),
                value.length(),
                value.md5sum(),
                value.hash());
    }
}
