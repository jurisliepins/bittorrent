package com.github.jurisliepins.torrent.handlers.command;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.TorrentState;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TorrentCommandTerminateHandler implements CoreContextSuccessHandler<TorrentState, TorrentCommand.Terminate> {

    @Override
    public NextState handle(
            final Context context,
            final TorrentState state,
            final Mailbox.Success mailbox,
            final TorrentCommand.Terminate message) {
        state.getAnnouncerRef().post(AnnouncerCommand.Terminate.INSTANCE, mailbox.self());
        state.getNotifiedRef().post(new TorrentNotification.Terminated(state.getInfoHash()), mailbox.self());
        return NextState.Terminate;
    }
}
