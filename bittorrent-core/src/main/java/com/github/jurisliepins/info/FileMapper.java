package com.github.jurisliepins.info;

import com.github.jurisliepins.BConstants;
import com.github.jurisliepins.BObjectMapper;
import com.github.jurisliepins.CoreException;
import com.github.jurisliepins.info.objects.FileBObject;
import com.github.jurisliepins.stream.BInputStream;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class FileMapper {
    public static File fromStream(@NonNull final BInputStream stream) {
        try {
            return fromObject(BObjectMapper.fromStream(stream, FileBObject.class));
        } catch (Exception e) {
            throw new CoreException("Failed to read file from stream", e);
        }
    }

    public static File fromBytes(final byte @NonNull [] bytes) {
        try {
            return fromObject(BObjectMapper.fromBytes(bytes, FileBObject.class));
        } catch (Exception e) {
            throw new CoreException("Failed to read file from bytes", e);
        }
    }

    public static File fromString(@NonNull final String string) {
        try {
            return fromObject(BObjectMapper.fromString(string, BConstants.DEFAULT_ENCODING, FileBObject.class));
        } catch (Exception e) {
            throw new CoreException("Failed to read file from bytes", e);
        }
    }

    public static File fromObject(@NonNull final FileBObject value) {
        return new File(value.length(), value.md5sum(), value.path());
    }
}
