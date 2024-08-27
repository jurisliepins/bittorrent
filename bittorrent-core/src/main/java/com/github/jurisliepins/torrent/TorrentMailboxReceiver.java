package com.github.jurisliepins.torrent;

import com.github.jurisliepins.MailboxReceiver;
import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import com.github.jurisliepins.types.StatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class TorrentMailboxReceiver implements MailboxReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(TorrentMailboxReceiver.class);

    private final ActorRef notifiedRef;

    private final TorrentState state;

    public TorrentMailboxReceiver(final ActorRef notifiedRef, final TorrentState state) {
        this.notifiedRef = Objects.requireNonNull(notifiedRef, "notifiedRef is null");
        this.state = Objects.requireNonNull(state, "state is null");
    }

    @Override
    public NextState receive(final Mailbox mailbox) {
        return switch (mailbox) {
            case Mailbox.Success mailboxSuccess -> handleSuccess(mailboxSuccess);
            case Mailbox.Failure mailboxFailure -> handleFailure(mailboxFailure);
        };
    }

    private NextState handleSuccess(final Mailbox.Success mailbox) {
        return switch (mailbox.message()) {
            case TorrentCommand command -> handleCommand(mailbox, command);
            default -> unhandled(mailbox);
        };
    }

    private NextState handleCommand(final Mailbox.Success mailbox, final TorrentCommand command) {
        return switch (command) {
            case TorrentCommand.Start start -> handleStartCommand(mailbox, start);
            case TorrentCommand.Stop stop -> handleStopCommand(mailbox, stop);
            case TorrentCommand.Terminate terminate -> handleTerminateCommand(mailbox, terminate);
        };
    }

    private NextState handleStartCommand(final Mailbox.Success mailbox, final TorrentCommand.Start command) {
        switch (state.getStatus()) {
            case Stopped -> {
                state.setStatus(StatusType.Started);
                LOGGER.info("[{}] Torrent started", state.getInfoHash());
                notifiedRef.post(new TorrentNotification.StatusChanged(state.getInfoHash(), StatusType.Started));
            }

            default -> LOGGER.info("[{}] Torrent already started", state.getInfoHash());
        }
        return NextState.Receive;
    }

    private NextState handleStopCommand(final Mailbox.Success mailbox, final TorrentCommand.Stop command) {
        switch (state.getStatus()) {
            case Started, Running, Errored -> {
                state.setStatus(StatusType.Stopped);
                LOGGER.info("[{}] Torrent stopped", state.getInfoHash());
                notifiedRef.post(new TorrentNotification.StatusChanged(state.getInfoHash(), StatusType.Stopped));
            }

            default -> LOGGER.info("[{}] Torrent already stopped", state.getInfoHash());
        }
        return NextState.Receive;
    }

    private NextState handleTerminateCommand(final Mailbox.Success mailbox, final TorrentCommand.Terminate command) {
        notifiedRef.post(new TorrentNotification.Terminated(state.getInfoHash()));
        return NextState.Terminate;
    }

    private NextState handleFailure(final Mailbox.Failure mailbox) {
        switch (mailbox.message()) {
            case TorrentCommand command -> {
                LOGGER.error("Failed to handle command", mailbox.cause());
                notifiedRef.post(new TorrentNotification.Failure(state.getInfoHash(), mailbox.cause()));
            }

            default -> LOGGER.error("Failed", mailbox.cause());
        }
        return NextState.Terminate;
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        LOGGER.error("[{}] Unhandled message {}", state.getInfoHash(), mailbox.message());
        return NextState.Receive;
    }
}