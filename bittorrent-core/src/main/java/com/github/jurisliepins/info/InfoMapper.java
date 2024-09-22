package com.github.jurisliepins.info;

import com.github.jurisliepins.BConstants;
import com.github.jurisliepins.BObjectMapper;
import com.github.jurisliepins.CoreException;
import com.github.jurisliepins.info.objects.InfoBObject;
import com.github.jurisliepins.stream.BInputStream;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Arrays;

@UtilityClass
public final class InfoMapper {
    public static Info fromStream(@NonNull final BInputStream stream) {
        try {
            return fromObject(BObjectMapper.fromStream(stream, InfoBObject.class));
        } catch (Exception e) {
            throw new CoreException("Failed to read info from stream", e);
        }
    }

    public static Info fromBytes(final byte @NonNull [] bytes) {
        try {
            return fromObject(BObjectMapper.fromBytes(bytes, InfoBObject.class));
        } catch (Exception e) {
            throw new CoreException("Failed to read info from bytes", e);
        }
    }

    public static Info fromString(@NonNull final String string) {
        try {
            return fromObject(BObjectMapper.fromString(string, BConstants.DEFAULT_ENCODING, InfoBObject.class));
        } catch (Exception e) {
            throw new CoreException("Failed to read info from bytes", e);
        }
    }

    public static Info fromObject(@NonNull final InfoBObject value) {
        if (value.files() != null && value.files().length > 0) {
            return new Info.MultiFileInfo(
                    value.pieceLength(),
                    value.pieces(),
                    value.isPrivate(),
                    value.name(),
                    Arrays.stream(value.files())
                            .map(file -> new File(file.length(),
                                                  file.md5sum(),
                                                  file.path()))
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
