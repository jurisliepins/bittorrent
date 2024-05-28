package com.github.jurisliepins.client;

import com.github.jurisliepins.info.InfoHash;

import java.util.HashMap;
import java.util.Map;

public final class  ClientState {
    private final Map<InfoHash, Object> torrents = new HashMap<>();

    public Map<InfoHash, Object> torrents() {
        return torrents;
    }
}
