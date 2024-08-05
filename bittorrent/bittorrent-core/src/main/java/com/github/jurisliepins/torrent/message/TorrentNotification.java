package com.github.jurisliepins.torrent.message;

import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.torrent.state.TorrentState;

import java.util.Objects;

public sealed interface TorrentNotification permits
        TorrentNotification.StatusChanged,
        TorrentNotification.Terminated {

    record StatusChanged(InfoHash infoHash, TorrentState.Status status) implements TorrentNotification {
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

}
