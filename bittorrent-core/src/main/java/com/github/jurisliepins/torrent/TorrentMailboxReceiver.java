package com.github.jurisliepins.torrent;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.CoreMailboxNotifiedLoggingReceiver;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import com.github.jurisliepins.types.StatusType;

import lombok.NonNull;

public final class TorrentMailboxReceiver extends CoreMailboxNotifiedLoggingReceiver<TorrentNotification> {
    private final TorrentState state;

    public TorrentMailboxReceiver(@NonNull final ActorRef notifiedRef, @NonNull final TorrentState state) {
        super(notifiedRef);
        this.state = state;
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
        return switch (state.getStatus()) {
            case Stopped -> {
                state.setStatus(StatusType.Started);
                yield receiveNext(new TorrentNotification.StatusChanged(state.getInfoHash(), StatusType.Started));
            }

            default -> {
                logger().info("[{}] Torrent already started", state.getInfoHash());
                yield receiveNext();
            }
        };
    }

    private NextState handleStopCommand(final Mailbox.Success mailbox, final TorrentCommand.Stop command) {
        return switch (state.getStatus()) {
            case Started, Running, Errored -> {
                state.setStatus(StatusType.Stopped);
                yield receiveNext(new TorrentNotification.StatusChanged(state.getInfoHash(), StatusType.Stopped));
            }

            default -> {
                logger().info("[{}] Torrent already stopped", state.getInfoHash());
                yield receiveNext();
            }
        };
    }

    private NextState handleTerminateCommand(final Mailbox.Success mailbox, final TorrentCommand.Terminate command) {
        return terminate(new TorrentNotification.Terminated(state.getInfoHash()));
    }

    private NextState handleFailure(final Mailbox.Failure mailbox) {
        return switch (mailbox.message()) {
            case TorrentCommand command -> {
                logger().error("[{}] Failed to handle command", state.getInfoHash(), mailbox.cause());
                yield receiveNext(new TorrentNotification.Failure(state.getInfoHash(), mailbox.cause()));
            }

            default -> {
                logger().error("[{}] Failed", state.getInfoHash(), mailbox.cause());
                yield receiveNext();
            }
        };
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        logger().error("[{}] Unhandled message {}", state.getInfoHash(), mailbox.message());
        return receiveNext();
    }
}
