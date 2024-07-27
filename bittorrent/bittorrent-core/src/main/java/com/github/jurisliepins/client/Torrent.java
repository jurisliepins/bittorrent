package com.github.jurisliepins.client;

import com.github.jurisliepins.info.InfoHash;

import java.util.Objects;

public record Torrent(
        Status status,
        InfoHash infoHash
) {
    public Torrent {
        Objects.requireNonNull(status, "status is null");
        Objects.requireNonNull(infoHash, "infoHash is null");
    }

    public enum Status {
        STARTED,
        STOPPED,
        ERRORED
    }
}
