package com.github.jurisliepins.torrent.handlers;

import com.github.jurisliepins.handler.CoreContextFailureHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.TorrentState;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TorrentFailureHandler implements CoreContextFailureHandler<TorrentState> {

    @Override
    public NextState handle(final Context context, final Mailbox.Failure mailbox, final TorrentState state) {
        switch (mailbox.message()) {
            case TorrentCommand ignored -> {
                log.error("[{}] Failed to handle command", state.getInfoHash(), mailbox.cause());
                state.getNotifiedRef().post(new TorrentNotification.Failure(state.getInfoHash(), mailbox.cause()), mailbox.self());
            }
            default -> {
                log.error("[{}] Failed", state.getInfoHash(), mailbox.cause());
                state.getNotifiedRef().post(new TorrentNotification.Failure(state.getInfoHash(), mailbox.cause()), mailbox.self());
            }
        }
        return NextState.Receive;
    }

}
