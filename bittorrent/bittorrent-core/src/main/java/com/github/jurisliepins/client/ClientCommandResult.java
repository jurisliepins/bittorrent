package com.github.jurisliepins.client;

import com.github.jurisliepins.info.InfoHash;

import java.util.Objects;

public sealed interface ClientCommandResult permits
        ClientCommandResult.Success,
        ClientCommandResult.Failure {

    record Success(InfoHash infoHash, String resultMessage) implements ClientCommandResult {
        public Success {
            Objects.requireNonNull(infoHash, "infoHash is null");
            Objects.requireNonNull(resultMessage, "resultMessage is null");
        }
    }

    record Failure(InfoHash infoHash, String resultMessage) implements ClientCommandResult {
        public Failure {
            Objects.requireNonNull(infoHash, "infoHash is null");
            Objects.requireNonNull(resultMessage, "resultMessage is null");
        }
    }
}
