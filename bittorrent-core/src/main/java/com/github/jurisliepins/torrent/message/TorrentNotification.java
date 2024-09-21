package com.github.jurisliepins.torrent.message;

import com.github.jurisliepins.info.Hash;
import com.github.jurisliepins.common.StatusType;
import lombok.NonNull;

public sealed interface TorrentNotification permits
        TorrentNotification.StatusChanged,
        TorrentNotification.Terminated,
        TorrentNotification.Failure {

    record StatusChanged(
            @NonNull Hash infoHash,
            @NonNull StatusType status
    ) implements TorrentNotification { }

    record Terminated(@NonNull Hash infoHash) implements TorrentNotification { }

    record Failure(
            @NonNull Hash infoHash,
            @NonNull Throwable cause
    ) implements TorrentNotification { }
}
