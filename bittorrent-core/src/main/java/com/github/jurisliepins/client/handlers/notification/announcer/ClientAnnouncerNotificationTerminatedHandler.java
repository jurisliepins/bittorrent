package com.github.jurisliepins.client.handlers.notification.announcer;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.context.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ClientAnnouncerNotificationTerminatedHandler
        implements CoreContextSuccessHandler<ClientState, AnnouncerNotification.Terminated> {

    @Override
    public NextState handle(
            final Context context,
            final Mailbox.Success mailbox,
            final ClientState state,
            final AnnouncerNotification.Terminated message) {
        switch (state.getTorrents().get(message.infoHash())) {
            case ClientState.Torrent torrent -> torrent.getRef().post(message, mailbox.sender());
            case null -> { /* Ignored. */ }
        }
        return NextState.Receive;
    }

}
