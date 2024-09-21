package com.github.jurisliepins.torrent.handlers.command;

import com.github.jurisliepins.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.common.StatusType;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.TorrentState;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;

public final class TorrentCommandStartHandler implements CoreContextSuccessHandler<TorrentState, TorrentCommand.Start> {

    @Override
    public NextState handle(
            final Context context,
            final Mailbox.Success mailbox,
            final TorrentState state,
            final TorrentCommand.Start message) {
        switch (state.getStatus()) {
            case Stopped -> {
                state.getAnnouncerRef()
                        .post(AnnouncerCommand.Start.INSTANCE, mailbox.self());
                state.setStatus(StatusType.Started);
                state.getNotifiedRef()
                        .post(new TorrentNotification.StatusChanged(state.getInfoHash(), StatusType.Started), mailbox.self());
                context.log()
                        .torrent()
                        .info("[{}] Torrent started", state.getInfoHash());
            }
            default -> {
                context.log()
                        .torrent()
                        .info("[{}] Torrent already started", state.getInfoHash());
            }
        }
        return NextState.Receive;
    }

}
