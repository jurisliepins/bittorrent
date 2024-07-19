package com.github.jurisliepins.core;

import com.github.jurisliepins.BitTorrentClient;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@Singleton
public final class Producers {

    @Produces
    public BitTorrentClient bitTorrentClient() {
        return new BitTorrentClient();
    }

}
