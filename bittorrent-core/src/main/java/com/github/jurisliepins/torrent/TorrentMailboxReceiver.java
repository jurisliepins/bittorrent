package com.github.jurisliepins.torrent;

import com.github.jurisliepins.CoreContextMailboxReceiver;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.message.TorrentCommand;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TorrentMailboxReceiver extends CoreContextMailboxReceiver {
    private final TorrentState state;

    public TorrentMailboxReceiver(
            @NonNull final Context context,
            @NonNull final TorrentState state) {
        super(context);
        this.state = state;
    }

    @Override
    public NextState receive(final Mailbox mailbox) {
        return switch (mailbox) {
            case Mailbox.Success success -> handle(success);
            case Mailbox.Failure failure -> handle(failure);
        };
    }

    private NextState handle(final Mailbox.Success mailbox) {
        return switch (mailbox.message()) {
            case TorrentCommand command -> handle(mailbox, command);
            case AnnouncerNotification notification -> handle(mailbox, notification);
            default -> unhandled(mailbox);
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final TorrentCommand command) {
        context().log()
                .torrent()
                .info("Handling command {}", command);
        return switch (command) {
            case TorrentCommand.Start start -> handle(mailbox, start);
            case TorrentCommand.Stop stop -> handle(mailbox, stop);
            case TorrentCommand.Terminate terminate -> handle(mailbox, terminate);
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final TorrentCommand.Start command) {
        return context().handlers()
                .torrent()
                .command()
                .start()
                .handle(context(), mailbox, state, command);
    }

    private NextState handle(final Mailbox.Success mailbox, final TorrentCommand.Stop command) {
        return context().handlers()
                .torrent()
                .command()
                .stop()
                .handle(context(), mailbox, state, command);
    }

    private NextState handle(final Mailbox.Success mailbox, final TorrentCommand.Terminate command) {
        return context().handlers()
                .torrent()
                .command()
                .terminate()
                .handle(context(), mailbox, state, command);
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification notification) {
        context().log()
                .torrent()
                .info("Handling announcer notification {}", notification);
        return switch (notification) {
            case AnnouncerNotification.PeersReceived peersReceived -> handle(mailbox, peersReceived);
            case AnnouncerNotification.StatusChanged statusChanged -> handle(mailbox, statusChanged);
            case AnnouncerNotification.Terminated terminated -> handle(mailbox, terminated);
            case AnnouncerNotification.Failure failure -> handle(mailbox, failure);
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification.PeersReceived peersReceived) {
        return context().handlers()
                .torrent()
                .notification()
                .announcer()
                .peersReceived()
                .handle(context(), mailbox, state, peersReceived);
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification.StatusChanged statusChanged) {
        return context().handlers()
                .torrent()
                .notification()
                .announcer()
                .statusChanged()
                .handle(context(), mailbox, state, statusChanged);
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification.Terminated terminated) {
        return context().handlers()
                .torrent()
                .notification()
                .announcer()
                .terminated()
                .handle(context(), mailbox, state, terminated);
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification.Failure failure) {
        return context().handlers()
                .torrent()
                .notification()
                .announcer()
                .failure()
                .handle(context(), mailbox, state, failure);
    }

    private NextState handle(final Mailbox.Failure mailbox) {
        return context().handlers()
                .torrent()
                .failure()
                .handle(context(), mailbox, state);
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        context().log()
                .torrent()
                .error("[{}] Unhandled message {}", state.getInfoHash(), mailbox.message());
        return NextState.Receive;
    }
}
