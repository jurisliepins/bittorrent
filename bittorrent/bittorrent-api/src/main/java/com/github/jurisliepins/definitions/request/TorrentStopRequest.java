package com.github.jurisliepins.definitions.request;

import com.github.jurisliepins.info.InfoHash;

import java.util.List;
import java.util.Objects;

public record TorrentStopRequest(List<InfoHash> infoHashes) {
    public TorrentStopRequest {
        Objects.requireNonNull(infoHashes, "infoHashes cannot be null");
    }
}
