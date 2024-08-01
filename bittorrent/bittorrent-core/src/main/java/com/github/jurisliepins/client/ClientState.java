package com.github.jurisliepins.client;

import com.github.jurisliepins.info.InfoHash;

public interface ClientState {

    ClientStateTorrent get(InfoHash infoHash);

    void add(ClientStateTorrent torrent);

    void remove(InfoHash infoHash);

}
