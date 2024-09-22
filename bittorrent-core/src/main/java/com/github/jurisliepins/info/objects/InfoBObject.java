package com.github.jurisliepins.info.objects;

import com.github.jurisliepins.BObjectMapper;
import com.github.jurisliepins.BProperty;
import com.github.jurisliepins.CoreException;
import com.github.jurisliepins.info.Hash;
import lombok.NonNull;

import java.security.MessageDigest;

public record InfoBObject(
        @BProperty("piece length") int pieceLength,
        @BProperty("pieces") byte @NonNull [] pieces,
        @BProperty("private") Boolean isPrivate,
        @BProperty("name") @NonNull String name,
        @BProperty("length") Long length,
        @BProperty("md5sum") String md5sum,
        @BProperty("files") FileBObject[] files
) {
    public Hash hash() {
        try {
            return new Hash(MessageDigest.getInstance("SHA-1").digest(BObjectMapper.toBytes(this)));
        } catch (Exception e) {
            throw new CoreException("Failed to generate hash", e);
        }
    }
}
