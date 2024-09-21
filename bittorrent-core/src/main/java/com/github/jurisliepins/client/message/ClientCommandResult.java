package com.github.jurisliepins.client.message;

import com.github.jurisliepins.info.Hash;
import lombok.NonNull;

public sealed interface ClientCommandResult permits
        ClientCommandResult.Success,
        ClientCommandResult.Failure {

    record Success(
            @NonNull Hash infoHash,
            @NonNull String message
    ) implements ClientCommandResult { }

    record Failure(
            @NonNull Hash infoHash,
            @NonNull String message
    ) implements ClientCommandResult { }
}
