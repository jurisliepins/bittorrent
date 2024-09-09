package com.github.jurisliepins.torrent.message;

import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.types.StatusType;
import lombok.NonNull;

public sealed interface TorrentNotification permits
        TorrentNotification.StatusChanged,
        TorrentNotification.Terminated,
        TorrentNotification.Failure {

    record StatusChanged(
            @NonNull InfoHash infoHash,
            @NonNull StatusType status
    ) implements TorrentNotification { }

    record Terminated(@NonNull InfoHash infoHash) implements TorrentNotification { }

    record Failure(
            @NonNull InfoHash infoHash,
            @NonNull Throwable cause
    ) implements TorrentNotification { }

    default InfoHash infoHash() {
        return switch (this) {
            case TorrentNotification.StatusChanged notification -> notification.infoHash();
            case TorrentNotification.Terminated notification -> notification.infoHash();
            case TorrentNotification.Failure notification -> notification.infoHash();
        };
    }
}
