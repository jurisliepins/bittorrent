package com.github.jurisliepins.definitions.request;

import com.github.jurisliepins.info.InfoHash;

import java.util.List;
import java.util.Objects;

public record TorrentRemoveRequest(List<InfoHash> infoHashes) {
    public TorrentRemoveRequest {
        Objects.requireNonNull(infoHashes, "infoHashes cannot be null");
    }
}
