package com.github.jurisliepins.torrent.message;

import com.github.jurisliepins.info.InfoHash;

import java.util.Objects;

public sealed interface TorrentCommandResult permits
        TorrentCommandResult.Success,
        TorrentCommandResult.Failure {

    record Success(InfoHash infoHash, String message) implements TorrentCommandResult {
        public Success {
            Objects.requireNonNull(infoHash, "hash is null");
            Objects.requireNonNull(message, "message is null");
        }
    }

    record Failure(InfoHash infoHash, String message) implements TorrentCommandResult {
        public Failure {
            Objects.requireNonNull(infoHash, "hash is null");
            Objects.requireNonNull(message, "message is null");
        }
    }
}
