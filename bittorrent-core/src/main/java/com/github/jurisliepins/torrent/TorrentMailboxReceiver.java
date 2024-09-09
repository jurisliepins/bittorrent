package com.github.jurisliepins.torrent;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.CoreMailboxNotifiedStateLoggingReceiver;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import com.github.jurisliepins.types.StatusType;

import lombok.NonNull;

public final class TorrentMailboxReceiver extends CoreMailboxNotifiedStateLoggingReceiver<TorrentNotification, TorrentState> {

    private final ActorRef announcerRef;

    public TorrentMailboxReceiver(
            @NonNull final ActorRef announcerRef,
            @NonNull final ActorRef notifiedRef,
            @NonNull final TorrentState state) {
        super(notifiedRef, state);
        this.announcerRef = announcerRef;
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
            case AnnouncerNotification notification -> handleAnnouncerNotification(mailbox, notification);
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
        return switch (state().getStatus()) {
            case Stopped -> {
                announcerRef.post(new AnnouncerCommand.Start());
                state().setStatus(StatusType.Started);
                yield receiveNext(new TorrentNotification.StatusChanged(state().getInfoHash(), StatusType.Started));
            }

            default -> {
                logger().info("[{}] Torrent already started", state().getInfoHash());
                yield receiveNext();
            }
        };
    }

    private NextState handleStopCommand(final Mailbox.Success mailbox, final TorrentCommand.Stop command) {
        return switch (state().getStatus()) {
            case Started, Running -> {
                announcerRef.post(new AnnouncerCommand.Stop());
                state().setStatus(StatusType.Stopped);
                yield receiveNext(new TorrentNotification.StatusChanged(state().getInfoHash(), StatusType.Stopped));
            }

            default -> {
                logger().info("[{}] Torrent already stopped", state().getInfoHash());
                yield receiveNext();
            }
        };
    }

    private NextState handleTerminateCommand(final Mailbox.Success mailbox, final TorrentCommand.Terminate command) {
        return terminate(new TorrentNotification.Terminated(state().getInfoHash()));
    }

    private NextState handleFailure(final Mailbox.Failure mailbox) {
        return switch (mailbox.message()) {
            case TorrentCommand command -> {
                logger().error("[{}] Failed to handle command", state().getInfoHash(), mailbox.cause());
                yield receiveNext(new TorrentNotification.Failure(state().getInfoHash(), mailbox.cause()));
            }

            default -> {
                logger().error("[{}] Failed", state().getInfoHash(), mailbox.cause());
                yield receiveNext();
            }
        };
    }

    private NextState handleAnnouncerNotification(final Mailbox.Success mailbox, final AnnouncerNotification notification) {
        logger().info("[{}] Handling announcer notification {}", state().getInfoHash(), notification);
        state().setStatus(StatusType.Running);
        return receiveNext(new TorrentNotification.StatusChanged(state().getInfoHash(), StatusType.Running));
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        logger().error("[{}] Unhandled message {}", state().getInfoHash(), mailbox.message());
        return receiveNext();
    }
}
