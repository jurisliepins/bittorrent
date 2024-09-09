package com.github.jurisliepins.announcer.message;

import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.types.StatusType;
import lombok.NonNull;

import java.net.InetSocketAddress;
import java.util.List;

public sealed interface AnnouncerNotification permits
        AnnouncerNotification.PeersReceived,
        AnnouncerNotification.StatusChanged,
        AnnouncerNotification.Terminated,
        AnnouncerNotification.Failure {

    record PeersReceived(
            @NonNull InfoHash infoHash,
            @NonNull List<InetSocketAddress> peers
    ) implements AnnouncerNotification { }

    record StatusChanged(
            @NonNull InfoHash infoHash,
            @NonNull StatusType status
    ) implements AnnouncerNotification { }

    record Terminated(@NonNull InfoHash infoHash) implements AnnouncerNotification { }

    record Failure(
            @NonNull InfoHash infoHash,
            @NonNull Throwable cause
    ) implements AnnouncerNotification { }

    default InfoHash infoHash() {
        return switch (this) {
            case AnnouncerNotification.PeersReceived notification -> notification.infoHash();
            case AnnouncerNotification.StatusChanged notification -> notification.infoHash();
            case AnnouncerNotification.Terminated notification -> notification.infoHash();
            case AnnouncerNotification.Failure notification -> notification.infoHash();
        };
    }
}
