package com.github.jurisliepins.client;

import com.github.jurisliepins.info.InfoHash;

import java.util.Objects;

public sealed interface ClientResponse permits
        ClientResponse.Get {

    record Get(InfoHash infoHash) implements ClientResponse {
        public Get {
            Objects.requireNonNull(infoHash, "infoHash is null");
        }
    }

}
