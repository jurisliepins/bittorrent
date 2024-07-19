package com.github.jurisliepins.client;

import com.github.jurisliepins.info.InfoHash;

import java.util.Objects;

public sealed interface ClientResponse permits
        ClientResponse.Get,
        ClientResponse.Failure {

    record Get(InfoHash infoHash) implements ClientResponse {
        public Get {
            Objects.requireNonNull(infoHash, "hash is null");
        }
    }

    record Failure(InfoHash infoHash, String resultMessage) implements ClientResponse {
        public Failure {
            Objects.requireNonNull(infoHash, "hash is null");
            Objects.requireNonNull(resultMessage, "message is null");
        }
    }

}
