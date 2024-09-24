package com.github.jurisliepins.utils;

import lombok.experimental.UtilityClass;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

@UtilityClass
public final class InfoUtils {

    public static final String UNI_FILE_TORRENT_FILE = "uni_file.torrent";
    public static final String MULTI_FILE_TORRENT_FILE = "multi_file.torrent";

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
