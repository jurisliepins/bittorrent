package com.github.jurisliepins.torrent;

import com.github.jurisliepins.CoreLoggingMailboxReceiver;
import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import com.github.jurisliepins.types.StatusType;

import java.util.Objects;

public final class TorrentMailboxReceiver extends CoreLoggingMailboxReceiver {
    private final ActorRef notifiedRef;

    private final TorrentState state;

    public TorrentMailboxReceiver(final ActorRef notifiedRef, final TorrentState state) {
        super(state.getInfoHash());
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
                logger().info("Torrent started");
                notifiedRef.post(new TorrentNotification.StatusChanged(state.getInfoHash(), StatusType.Started));
            }

            default -> logger().info("Torrent already started");
        }
        return NextState.Receive;
    }

    private NextState handleStopCommand(final Mailbox.Success mailbox, final TorrentCommand.Stop command) {
        switch (state.getStatus()) {
            case Started, Running, Errored -> {
                state.setStatus(StatusType.Stopped);
                logger().info("Torrent stopped");
                notifiedRef.post(new TorrentNotification.StatusChanged(state.getInfoHash(), StatusType.Stopped));
            }

            default -> logger().info("Torrent already stopped");
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
                logger().error("Failed to handle command", mailbox.cause());
                notifiedRef.post(new TorrentNotification.Failure(state.getInfoHash(), mailbox.cause()));
            }

            default -> logger().error("Failed", mailbox.cause());
        }
        return NextState.Terminate;
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        logger().error("Unhandled message {}", mailbox.message());
        return NextState.Receive;
    }
}
