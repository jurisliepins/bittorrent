package com.github.jurisliepins.tracker;

import lombok.NonNull;

import java.net.InetSocketAddress;
import java.util.List;

public sealed interface TrackerResponse permits
        TrackerResponse.Success,
        TrackerResponse.Failure {

    record Success(
            Long complete,
            Long incomplete,
            @NonNull Long interval,
            Long minInterval,
            @NonNull List<InetSocketAddress> peers,
            String trackerId,
            String warningMessage
    ) implements TrackerResponse { }

    record Failure(
            @NonNull String failureReason
    ) implements TrackerResponse { }
}
