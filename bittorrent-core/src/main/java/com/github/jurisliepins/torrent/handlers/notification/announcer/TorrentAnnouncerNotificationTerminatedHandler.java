package com.github.jurisliepins.torrent.handlers.notification.announcer;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.TorrentState;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TorrentAnnouncerNotificationTerminatedHandler
        implements CoreContextSuccessHandler<TorrentState, AnnouncerNotification.Terminated> {

    @Override
    public NextState handle(
            final Context context,
            final TorrentState state,
            final Mailbox.Success mailbox,
            final AnnouncerNotification.Terminated message) {
        return NextState.Receive;
    }
}
