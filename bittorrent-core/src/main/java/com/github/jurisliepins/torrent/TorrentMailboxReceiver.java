package com.github.jurisliepins.torrent;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.CoreMailboxNotifiedStateContextLoggingReceiver;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import com.github.jurisliepins.types.StatusType;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TorrentMailboxReceiver extends CoreMailboxNotifiedStateContextLoggingReceiver<TorrentState, TorrentNotification> {

    private final ActorRef announcerRef;

    public TorrentMailboxReceiver(
            @NonNull final Context context,
            @NonNull final TorrentState state,
            @NonNull final ActorRef notifiedRef,
            @NonNull final ActorRef announcerRef) {
        super(context, state, notifiedRef);
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
                announcerRef.post(AnnouncerCommand.Start.INSTANCE);
                yield receiveNext(StatusType.Started, new TorrentNotification.StatusChanged(state().getInfoHash(), StatusType.Started));
            }

            default -> {
                log.info("[{}] Torrent already started", state().getInfoHash());
                yield receiveNext();
            }
        };
    }

    private NextState handleStopCommand(final Mailbox.Success mailbox, final TorrentCommand.Stop command) {
        return switch (state().getStatus()) {
            case Started -> {
                announcerRef.post(AnnouncerCommand.Stop.INSTANCE);
                yield receiveNext(StatusType.Stopped, new TorrentNotification.StatusChanged(state().getInfoHash(), StatusType.Stopped));
            }

            default -> {
                log.info("[{}] Torrent already stopped", state().getInfoHash());
                yield receiveNext();
            }
        };
    }

    private NextState handleTerminateCommand(final Mailbox.Success mailbox, final TorrentCommand.Terminate command) {
        announcerRef.post(AnnouncerCommand.Terminate.INSTANCE);
        return terminate(new TorrentNotification.Terminated(state().getInfoHash()));
    }

    private NextState handleFailure(final Mailbox.Failure mailbox) {
        return switch (mailbox.message()) {
            case TorrentCommand command -> {
                log.error("[{}] Failed to handle command", state().getInfoHash(), mailbox.cause());
                yield receiveNext(new TorrentNotification.Failure(state().getInfoHash(), mailbox.cause()));
            }

            default -> {
                log.error("[{}] Failed", state().getInfoHash(), mailbox.cause());
                yield receiveNext(new TorrentNotification.Failure(state().getInfoHash(), mailbox.cause()));
            }
        };
    }

    private NextState handleAnnouncerNotification(final Mailbox.Success mailbox, final AnnouncerNotification notification) {
        log.info("[{}] Handling announcer notification {}", state().getInfoHash(), notification);
        switch (notification) {
            case AnnouncerNotification.PeersReceived peersReceived -> {
                log.info("[{}] Received peers {}", state().getInfoHash(), peersReceived.peers());
                // TODO: !
            }
            case AnnouncerNotification.StatusChanged statusChanged -> { /* Ignored. */ }
            case AnnouncerNotification.Terminated terminated -> { /* Ignored. */ }
            case AnnouncerNotification.Failure failure -> { /* Ignored. */ }
        }
        return receiveNext();
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        log.error("[{}] Unhandled message {}", state().getInfoHash(), mailbox.message());
        return receiveNext();
    }
}
