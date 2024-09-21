package com.github.jurisliepins.client.handlers.notification.announcer;

import com.github.jurisliepins.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.context.Context;

public final class ClientAnnouncerNotificationStatusChangedHandler
        implements CoreContextSuccessHandler<ClientState, AnnouncerNotification.StatusChanged> {

    @Override
    public NextState handle(
            final Context context,
            final Mailbox.Success mailbox,
            final ClientState state,
            final AnnouncerNotification.StatusChanged message) {
        switch (state.getTorrents().get(message.infoHash())) {
            case ClientState.Torrent torrent -> {
                torrent.getRef().post(message, mailbox.sender());
            }
            case null -> {
                /* Ignored. */
            }
        }
        return NextState.Receive;
    }

}
