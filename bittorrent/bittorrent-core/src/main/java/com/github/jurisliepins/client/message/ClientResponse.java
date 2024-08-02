package com.github.jurisliepins.client.message;

import com.github.jurisliepins.client.state.ClientStateTorrent;
import com.github.jurisliepins.info.InfoHash;

import java.util.Objects;

public sealed interface ClientResponse permits
        ClientResponse.Get,
        ClientResponse.Failure {

    record Get(ClientStateTorrent torrent) implements ClientResponse {
        public Get {
            Objects.requireNonNull(torrent, "torrent is null");
        }
    }

    record Failure(InfoHash infoHash, String resultMessage) implements ClientResponse {
        public Failure {
            Objects.requireNonNull(infoHash, "hash is null");
            Objects.requireNonNull(resultMessage, "message is null");
        }
    }

}
