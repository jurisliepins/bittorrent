package com.github.jurisliepins.torrent.handlers.notification.announcer;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.TorrentState;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public final class TorrentAnnouncerNotificationPeersReceivedHandler
        implements CoreContextSuccessHandler<TorrentState, AnnouncerNotification.PeersReceived> {

    @Override
    public NextState handle(
            final Context context,
            final TorrentState state,
            final Mailbox.Success mailbox,
            final AnnouncerNotification.PeersReceived message) {
        log.info("[{}] Received peers {}", state.getInfoHash(), message.peers());

        var connections = message.peers()
                .parallelStream()
                .map(peer -> {
                    try {
                        var connection = context.io()
                                .connectionFactory()
                                .connect(peer);
                        log.info("[{}] Connected to peer {}", state.getInfoHash(), connection.remoteEndpoint());
                        return Optional.of(connection);
                    } catch (Exception e) {
                        log.error("[{}] Failed to connect to peer {}", state.getInfoHash(), peer, e);
                        return Optional.empty();
                    }
                })
                .collect(Collectors.toSet());

        return NextState.Receive;
    }
}
