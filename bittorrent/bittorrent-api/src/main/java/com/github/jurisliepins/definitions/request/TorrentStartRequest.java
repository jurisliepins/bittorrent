package com.github.jurisliepins.definitions.request;

import com.github.jurisliepins.info.InfoHash;

import java.util.List;
import java.util.Objects;

public record TorrentStartRequest(List<InfoHash> infoHashes) {
    public TorrentStartRequest {
        Objects.requireNonNull(infoHashes, "infoHashes cannot be null");
    }
}
