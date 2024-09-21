package com.github.jurisliepins.announcer.message;

import com.github.jurisliepins.common.StatusType;
import com.github.jurisliepins.info.Hash;
import lombok.NonNull;

import java.net.InetSocketAddress;
import java.util.List;

public sealed interface AnnouncerNotification permits
        AnnouncerNotification.PeersReceived,
        AnnouncerNotification.StatusChanged,
        AnnouncerNotification.Terminated,
        AnnouncerNotification.Failure {

    record PeersReceived(
            @NonNull Hash infoHash,
            @NonNull List<InetSocketAddress> peers
    ) implements AnnouncerNotification { }

    record StatusChanged(
            @NonNull Hash infoHash,
            @NonNull StatusType status
    ) implements AnnouncerNotification { }

    record Terminated(@NonNull Hash infoHash) implements AnnouncerNotification { }

    record Failure(
            @NonNull Hash infoHash,
            @NonNull Throwable cause
    ) implements AnnouncerNotification { }
}
