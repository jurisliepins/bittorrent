package com.github.jurisliepins.client.message;

import com.github.jurisliepins.info.InfoHash;
import lombok.NonNull;

public sealed interface ClientCommandResult permits
        ClientCommandResult.Success,
        ClientCommandResult.Failure {

    record Success(
            @NonNull InfoHash infoHash,
            @NonNull String message
    ) implements ClientCommandResult { }

    record Failure(
            @NonNull InfoHash infoHash,
            @NonNull String message
    ) implements ClientCommandResult { }
}
