package com.github.jurisliepins.client;

import com.github.jurisliepins.BitTorrentClient;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@Singleton
public final class ClientProducer {

    @Produces
    public BitTorrentClient bitTorrentClient() {
        return new BitTorrentClient();
    }

}
