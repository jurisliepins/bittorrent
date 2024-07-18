package com.github.jurisliepins.client;

import com.github.jurisliepins.info.InfoHash;

import java.util.Objects;

public sealed interface ClientCommand permits
        ClientCommand.Add,
        ClientCommand.Remove,
        ClientCommand.Start,
        ClientCommand.Stop {

    record Add(byte[] metaInfo) implements ClientCommand {
        public Add {
            Objects.requireNonNull(metaInfo, "metaInfo is null");
        }
    }

    record Remove(InfoHash infoHash) implements ClientCommand {
        public Remove {
            Objects.requireNonNull(infoHash, "hash is null");
        }
    }

    record Start(InfoHash infoHash) implements ClientCommand {
        public Start {
            Objects.requireNonNull(infoHash, "hash is null");
        }
    }

    record Stop(InfoHash infoHash) implements ClientCommand {
        public Stop {
            Objects.requireNonNull(infoHash, "hash is null");
        }
    }
}
