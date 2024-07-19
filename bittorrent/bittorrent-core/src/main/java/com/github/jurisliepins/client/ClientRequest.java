package com.github.jurisliepins.client;

import com.github.jurisliepins.info.InfoHash;

import java.util.Objects;

public sealed interface ClientRequest permits
        ClientRequest.Get {

    record Get(InfoHash infoHash) implements ClientRequest {
        public Get {
            Objects.requireNonNull(infoHash, "hash is null");
        }
    }

}
