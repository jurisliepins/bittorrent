package com.github.jurisliepins.torrent;

import com.github.jurisliepins.CoreContextMailboxReceiver;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
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

    private NextState handle(final Mailbox.Failure mailbox) {
        return context().handlers()
                .torrent()
                .failure()
                .handle(context(), mailbox, state);
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        context().log()
                .client()
                .error("[{}] Unhandled message {}", state.getInfoHash(), mailbox.message());
        return NextState.Receive;
    }
}
