package com.github.jurisliepins.client.handlers.notification.torrent;

import com.github.jurisliepins.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.message.TorrentNotification;

public final class ClientTorrentNotificationStatusChangedHandler
        implements CoreContextSuccessHandler<ClientState, TorrentNotification.StatusChanged> {

    @Override
    public NextState handle(
            final Context context,
            final Mailbox.Success mailbox,
            final ClientState state,
            final TorrentNotification.StatusChanged message) {
        switch (state.getTorrents().get(message.infoHash())) {
            case ClientState.Torrent torrent -> {
                torrent.setStatus(message.status());
            }
            case null -> {
                context.log()
                        .client()
                        .error("Torrent notification for non-existent torrent '{}'", message.infoHash());
            }
        }
        return NextState.Receive;
    }

}
