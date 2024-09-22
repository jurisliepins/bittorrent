package com.github.jurisliepins.info;

import com.github.jurisliepins.BConstants;
import com.github.jurisliepins.BObjectMapper;
import com.github.jurisliepins.CoreException;
import com.github.jurisliepins.info.objects.MetaInfoBObject;
import com.github.jurisliepins.stream.BInputStream;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class MetaInfoMapper {
    public static MetaInfo fromStream(@NonNull final BInputStream stream) {
        try {
            return fromObject(BObjectMapper.fromStream(stream, MetaInfoBObject.class));
        } catch (Exception e) {
            throw new CoreException("Failed to read meta-info from stream", e);
        }
    }

    public static MetaInfo fromBytes(final byte @NonNull [] bytes) {
        try {
            return fromObject(BObjectMapper.fromBytes(bytes, MetaInfoBObject.class));
        } catch (Exception e) {
            throw new CoreException("Failed to read meta-info from bytes", e);
        }
    }

    public static MetaInfo fromString(@NonNull final String string) {
        try {
            return fromObject(BObjectMapper.fromString(string, BConstants.DEFAULT_ENCODING, MetaInfoBObject.class));
        } catch (Exception e) {
            throw new CoreException("Failed to read meta-info from string", e);
        }
    }

    public static MetaInfo fromObject(@NonNull final MetaInfoBObject value) {
        return new MetaInfo(
                InfoMapper.fromObject(value.info()),
                value.announce(),
                value.announceList(),
                value.creationDate(),
                value.comment(),
                value.createdBy(),
                value.encoding());
    }
}
