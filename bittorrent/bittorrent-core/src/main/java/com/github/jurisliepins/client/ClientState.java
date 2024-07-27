package com.github.jurisliepins.client;

import com.github.jurisliepins.info.InfoHash;

public interface ClientState {

    Torrent get(InfoHash infoHash);

    void add(Torrent torrent);

    void remove(InfoHash infoHash);

}
