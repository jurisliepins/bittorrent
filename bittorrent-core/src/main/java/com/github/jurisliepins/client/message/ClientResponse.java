package com.github.jurisliepins.client.message;

import com.github.jurisliepins.bitfield.ImmutableBitfield;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.types.StatusType;

import java.util.Objects;

public sealed interface ClientResponse permits
        ClientResponse.Get,
        ClientResponse.Failure {

    record Torrent(
            StatusType status,
            InfoHash infoHash,
//            Object peerId,
            ImmutableBitfield bitfield,
            String name,
            long length,
            long downloaded,
            long uploaded,
            long left,
            double downloadRate,
            double uploadRate
    ) {
        public Torrent {
            Objects.requireNonNull(status, "status is null");
            Objects.requireNonNull(infoHash, "infoHash is null");
//            Objects.requireNonNull(peerId, "peerId is null");
            Objects.requireNonNull(bitfield, "bitfield is null");
            Objects.requireNonNull(name, "name is null");
        }
    }

    record Get(Object torrent) implements ClientResponse {
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
