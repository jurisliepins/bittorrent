package com.github.jurisliepins.client.handlers.notification.torrent;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ClientTorrentNotificationFailureHandler
        implements CoreContextSuccessHandler<ClientState, TorrentNotification.Failure> {

    @Override
    public NextState handle(
            final Context context,
            final ClientState state,
            final Mailbox.Success mailbox,
            final TorrentNotification.Failure message) {
        log.error("Torrent '{}' failed", message.infoHash());
        return NextState.Receive;
    }
}
