package com.github.jurisliepins.info;

import com.github.jurisliepins.info.objects.FileBObject;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class FileMapper {
    public static File fromObject(@NonNull final FileBObject value) {
        return new File(value.length(), value.md5sum(), value.path());
    }
}
