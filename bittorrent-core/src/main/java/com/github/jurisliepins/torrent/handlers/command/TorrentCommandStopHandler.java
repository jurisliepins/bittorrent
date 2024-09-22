package com.github.jurisliepins.torrent.handlers.command;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.common.StatusType;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.TorrentState;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TorrentCommandStopHandler implements CoreContextSuccessHandler<TorrentState, TorrentCommand.Stop> {

    @Override
    public NextState handle(
            final Context context,
            final TorrentState state,
            final Mailbox.Success mailbox,
            final TorrentCommand.Stop message) {
        switch (state.getStatus()) {
            case Started, Errored -> {
                state.getAnnouncerRef().post(AnnouncerCommand.Stop.INSTANCE, mailbox.self());

                state.setStatus(StatusType.Stopped);
                state.getNotifiedRef().post(new TorrentNotification.StatusChanged(state.getInfoHash(), StatusType.Stopped), mailbox.self());

                log.info("[{}] Torrent stopped", state.getInfoHash());
            }
            default -> log.info("[{}] Torrent already stopped", state.getInfoHash());
        }
        return NextState.Receive;
    }
}
