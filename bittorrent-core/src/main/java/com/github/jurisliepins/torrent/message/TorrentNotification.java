package com.github.jurisliepins.torrent.message;

import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.types.StatusType;

import java.util.Objects;

public sealed interface TorrentNotification permits
        TorrentNotification.StatusChanged,
        TorrentNotification.Terminated,
        TorrentNotification.Failure {

    record StatusChanged(InfoHash infoHash, StatusType status) implements TorrentNotification {
        public StatusChanged {
            Objects.requireNonNull(infoHash, "infoHash is null");
            Objects.requireNonNull(status, "status is null");
        }
    }

    record Terminated(InfoHash infoHash) implements TorrentNotification {
        public Terminated {
            Objects.requireNonNull(infoHash, "infoHash is null");
        }
    }

    record Failure(InfoHash infoHash, Throwable cause) implements TorrentNotification {
        public Failure {
            Objects.requireNonNull(infoHash, "hash is null");
            Objects.requireNonNull(cause, "cause is null");
        }
    }
}
