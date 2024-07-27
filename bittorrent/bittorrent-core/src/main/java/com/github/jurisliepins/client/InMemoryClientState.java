package com.github.jurisliepins.client;

import com.github.jurisliepins.info.InfoHash;

import java.util.HashMap;
import java.util.Map;

public final class InMemoryClientState implements ClientState {

    private final Map<InfoHash, Torrent> torrents = new HashMap<>();

    @Override
    public Torrent get(final InfoHash infoHash) {
        return torrents.get(infoHash);
    }

    @Override
    public void add(final Torrent torrent) {
        torrents.put(torrent.infoHash(), torrent);
    }

    @Override
    public void remove(final InfoHash infoHash) {
        torrents.remove(infoHash);
    }
}
