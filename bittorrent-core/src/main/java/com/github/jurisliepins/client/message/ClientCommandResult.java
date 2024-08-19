package com.github.jurisliepins.client.message;

import com.github.jurisliepins.info.InfoHash;

import java.util.Objects;

public sealed interface ClientCommandResult permits
        ClientCommandResult.Success,
        ClientCommandResult.Failure {

    record Success(InfoHash infoHash, String message) implements ClientCommandResult {
        public Success {
            Objects.requireNonNull(infoHash, "hash is null");
            Objects.requireNonNull(message, "message is null");
        }
    }

    record Failure(InfoHash infoHash, String message) implements ClientCommandResult {
        public Failure {
            Objects.requireNonNull(infoHash, "hash is null");
            Objects.requireNonNull(message, "message is null");
        }
    }
}
