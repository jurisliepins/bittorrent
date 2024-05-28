package com.github.jurisliepins.client;

import com.github.jurisliepins.info.InfoHash;

import java.util.Objects;

public sealed interface ClientCommand permits
        ClientCommand.Add,
        ClientCommand.Remove,
        ClientCommand.Start,
        ClientCommand.Stop {

    record Add(InfoHash torrent) implements ClientCommand {
        public Add {
            Objects.requireNonNull(torrent, "torrent is null");
        }
    }

    record Remove(InfoHash infoHash) implements ClientCommand {
        public Remove {
            Objects.requireNonNull(infoHash, "infoHash is null");
        }
    }

    record Start(InfoHash infoHash) implements ClientCommand {
        public Start {
            Objects.requireNonNull(infoHash, "infoHash is null");
        }
    }

    record Stop(InfoHash infoHash) implements ClientCommand {
        public Stop {
            Objects.requireNonNull(infoHash, "infoHash is null");
        }
    }
}
