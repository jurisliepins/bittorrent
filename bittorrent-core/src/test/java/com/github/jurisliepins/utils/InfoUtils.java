package com.github.jurisliepins.utils;

import com.github.jurisliepins.info.Hash;
import lombok.experimental.UtilityClass;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

@UtilityClass
public final class InfoUtils {

    public static final String UNI_FILE_TORRENT_FILE = "uni_file.torrent";
    public static final String MULTI_FILE_TORRENT_FILE = "multi_file.torrent";

    public static final Hash UNI_FILE_INFO_HASH = new Hash("aa171ca77f14f55d6ec23d7e9541b7791e6c383c");
    public static final Hash MULTI_FILE_INFO_HASH = new Hash("6e540ebbc92131138746231ff3e44f165fd3b373");

    public static byte[] readUniFileTorrent() throws IOException {
        return readFileAsBytes(UNI_FILE_TORRENT_FILE);
    }

    public static byte[] readMultiFileTorrent() throws IOException {
        return readFileAsBytes(MULTI_FILE_TORRENT_FILE);
    }

    public static byte[] readFileAsBytes(final String name) throws IOException {
        return requireNonNull(InfoUtils.class.getClassLoader().getResourceAsStream(name)).readAllBytes();
    }
}
