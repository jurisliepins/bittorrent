package com.github.jurisliepins.client.handlers.notification.torrent;

import com.github.jurisliepins.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.message.TorrentNotification;

public final class ClientTorrentNotificationFailureHandler
        implements CoreContextSuccessHandler<ClientState, TorrentNotification.Failure> {

    @Override
    public NextState handle(
            final Context context,
            final Mailbox.Success mailbox,
            final ClientState state,
            final TorrentNotification.Failure message) {
        context.log()
                .client()
                .error("Torrent '{}' failed", message.infoHash());
        return NextState.Receive;
    }

}
