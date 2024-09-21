package com.github.jurisliepins.client.handlers.notification.torrent;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ClientTorrentNotificationTerminatedHandler
        implements CoreContextSuccessHandler<ClientState, TorrentNotification.Terminated> {

    @Override
    public NextState handle(
            final Context context,
            final Mailbox.Success mailbox,
            final ClientState state,
            final TorrentNotification.Terminated message) {
        switch (state.getTorrents().remove(message.infoHash())) {
            case ClientState.Torrent ignored -> { /* Ignored. */ }
            case null -> log.error("Torrent notification for non-existent torrent '{}'", message.infoHash());
        }
        return NextState.Receive;
    }

}
