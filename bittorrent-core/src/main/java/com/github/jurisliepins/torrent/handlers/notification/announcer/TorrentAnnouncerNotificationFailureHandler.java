package com.github.jurisliepins.torrent.handlers.notification.announcer;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.TorrentState;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TorrentAnnouncerNotificationFailureHandler
        implements CoreContextSuccessHandler<TorrentState, AnnouncerNotification.Failure> {

    @Override
    public NextState handle(
            final Context context,
            final Mailbox.Success mailbox,
            final TorrentState state,
            final AnnouncerNotification.Failure message) {
        return NextState.Receive;
    }

}