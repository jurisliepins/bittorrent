package com.github.jurisliepins.torrent.handlers;

import com.github.jurisliepins.CoreContextFailureHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.TorrentState;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;

public final class TorrentFailureHandler implements CoreContextFailureHandler<TorrentState> {

    @Override
    public NextState handle(final Context context, final Mailbox.Failure mailbox, final TorrentState state) {
        return switch (mailbox.message()) {
            case TorrentCommand ignored -> {
                context.log()
                        .torrent()
                        .error("[{}] Failed to handle command", state.getInfoHash(), mailbox.cause());
                state.getNotifiedRef()
                        .post(new TorrentNotification.Failure(state.getInfoHash(), mailbox.cause()), mailbox.self());
                yield NextState.Receive;
            }
            default -> {
                context.log()
                        .torrent()
                        .error("[{}] Failed", state.getInfoHash(), mailbox.cause());
                state.getNotifiedRef()
                        .post(new TorrentNotification.Failure(state.getInfoHash(), mailbox.cause()), mailbox.self());
                yield NextState.Receive;
            }
        };
    }

}
