package com.github.jurisliepins.torrent;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.MailboxReceiver;
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
public final class TorrentMailboxReceiver implements MailboxReceiver {

    private final Context context;
    private final TorrentState state;
    private final ActorRef notifiedRef;
    private final ActorRef announcerRef;

    public TorrentMailboxReceiver(
            @NonNull final Context context,
            @NonNull final TorrentState state,
            @NonNull final ActorRef notifiedRef,
            @NonNull final ActorRef announcerRef) {
        this.context = context;
        this.state = state;
        this.notifiedRef = notifiedRef;
        this.announcerRef = announcerRef;
    }

    @Override
    public NextState receive(final Mailbox mailbox) {
        return switch (mailbox) {
            case Mailbox.Success m -> handle(m);
            case Mailbox.Failure m -> handle(m);
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
        return switch (command) {
            case TorrentCommand.Start start -> handle(mailbox, start);
            case TorrentCommand.Stop stop -> handle(mailbox, stop);
            case TorrentCommand.Terminate terminate -> handle(mailbox, terminate);
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final TorrentCommand.Start command) {
        return switch (state.getStatus()) {
            case Stopped -> {
                announcerRef.post(AnnouncerCommand.Start.INSTANCE);
                state.setStatus(StatusType.Started);
                notifiedRef.post(new TorrentNotification.StatusChanged(state.getInfoHash(), StatusType.Started), mailbox.self());
                yield NextState.Receive;
            }

            default -> {
                log.info("[{}] Torrent already started", state.getInfoHash());
                yield NextState.Receive;
            }
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final TorrentCommand.Stop command) {
        return switch (state.getStatus()) {
            case Started -> {
                announcerRef.post(AnnouncerCommand.Stop.INSTANCE);
                state.setStatus(StatusType.Stopped);
                notifiedRef.post(new TorrentNotification.StatusChanged(state.getInfoHash(), StatusType.Stopped), mailbox.self());
                yield NextState.Receive;
            }

            default -> {
                log.info("[{}] Torrent already stopped", state.getInfoHash());
                yield NextState.Receive;
            }
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final TorrentCommand.Terminate command) {
        announcerRef.post(AnnouncerCommand.Terminate.INSTANCE);
        notifiedRef.post(new TorrentNotification.Terminated(state.getInfoHash()), mailbox.self());
        return NextState.Terminate;
    }

    private NextState handle(final Mailbox.Failure mailbox) {
        return switch (mailbox.message()) {
            case TorrentCommand command -> {
                log.error("[{}] Failed to handle command", state.getInfoHash(), mailbox.cause());
                notifiedRef.post(new TorrentNotification.Failure(state.getInfoHash(), mailbox.cause()), mailbox.self());
                yield NextState.Receive;
            }

            default -> {
                log.error("[{}] Failed", state.getInfoHash(), mailbox.cause());
                notifiedRef.post(new TorrentNotification.Failure(state.getInfoHash(), mailbox.cause()), mailbox.self());
                yield NextState.Receive;
            }
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification notification) {
        log.info("[{}] Handling announcer notification {}", state.getInfoHash(), notification);
        switch (notification) {
            case AnnouncerNotification.PeersReceived peersReceived -> {
                log.info("[{}] Received peers {}", state.getInfoHash(), peersReceived.peers());
            }
            case AnnouncerNotification.StatusChanged statusChanged -> { /* Ignored. */ }
            case AnnouncerNotification.Terminated terminated -> { /* Ignored. */ }
            case AnnouncerNotification.Failure failure -> { /* Ignored. */ }
        }
        return NextState.Receive;
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        log.error("[{}] Unhandled message {}", state.getInfoHash(), mailbox.message());
        return NextState.Receive;
    }
}
