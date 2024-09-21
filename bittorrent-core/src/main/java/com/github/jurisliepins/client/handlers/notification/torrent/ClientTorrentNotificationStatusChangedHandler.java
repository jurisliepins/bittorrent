package com.github.jurisliepins.client.handlers.notification.torrent;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ClientTorrentNotificationStatusChangedHandler
        implements CoreContextSuccessHandler<ClientState, TorrentNotification.StatusChanged> {

    @Override
    public NextState handle(
            final Context context,
            final Mailbox.Success mailbox,
            final ClientState state,
            final TorrentNotification.StatusChanged message) {
        switch (state.getTorrents().get(message.infoHash())) {
            case ClientState.Torrent torrent -> torrent.setStatus(message.status());
            case null -> log.error("Torrent notification for non-existent torrent '{}'", message.infoHash());
        }
        return NextState.Receive;
    }

}
