package com.github.jurisliepins.client.state;

import com.github.jurisliepins.info.InfoHash;

import java.util.HashMap;
import java.util.Map;

public final class ClientState {

    private final Map<InfoHash, ClientStateTorrent> torrents = new HashMap<>();

    public ClientStateTorrent get(final InfoHash infoHash) {
        return torrents.get(infoHash);
    }

    public void add(final ClientStateTorrent torrent) {
        torrents.put(torrent.getInfoHash(), torrent);
    }

    public void remove(final InfoHash infoHash) {
        torrents.remove(infoHash);
    }
}
