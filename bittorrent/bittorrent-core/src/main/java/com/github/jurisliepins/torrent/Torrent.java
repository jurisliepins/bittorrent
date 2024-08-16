package com.github.jurisliepins.torrent;

import com.github.jurisliepins.ActorReceiver;
import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.client.Client;
import com.github.jurisliepins.mailbox.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.log.Log;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import com.github.jurisliepins.torrent.state.TorrentState;

import java.util.Objects;

public final class Torrent implements ActorReceiver {

    private final ActorRef notifiedRef;

    private final TorrentState state;

    public Torrent(final ActorRef notifiedRef, final TorrentState state) {
        this.notifiedRef = Objects.requireNonNull(notifiedRef, "notifiedRef is null");
        this.state = Objects.requireNonNull(state, "state is null");
    }

    @Override
    public NextState receive(final Mailbox mailbox) {
        return switch (mailbox.status()) {
            case Success -> handleSuccess(mailbox);
            case Failure -> handleFailure(mailbox);
        };
    }

    private NextState handleSuccess(final Mailbox mailbox) {
        return switch (mailbox.message()) {
            case TorrentCommand command -> handleCommand(mailbox, command);
            default -> unhandled(mailbox.message());
        };
    }

    private NextState handleCommand(final Mailbox mailbox, final TorrentCommand command) {
        return switch (command) {
            case TorrentCommand.Start start -> handleStartCommand(mailbox, start);
            case TorrentCommand.Stop stop -> handleStopCommand(mailbox, stop);
            case TorrentCommand.Terminate terminate -> handleTerminateCommand(mailbox, terminate);
        };
    }

    private NextState handleStartCommand(final Mailbox mailbox, final TorrentCommand.Start command) {
        Log.debug(Torrent.class, "[{}] Handling start command {}", state.getInfoHash(), command);

        switch (state.getStatus()) {
            case Stopped -> {
                state.setStatus(TorrentState.Status.Started);
                Log.info(Torrent.class, "[{}] Torrent started", state.getInfoHash());
                notifiedRef.post(new TorrentNotification.StatusChanged(state.getInfoHash(), TorrentState.Status.Started));
            }
            default -> Log.debug(Torrent.class, "[{}] Torrent already started", state.getInfoHash());
        }
        return NextState.Receive;
    }

    private NextState handleStopCommand(final Mailbox mailbox, final TorrentCommand.Stop command) {
        Log.debug(Torrent.class, "[{}] Handling stop command {}", state.getInfoHash(), command);

        switch (state.getStatus()) {
            case Started,
                 Running,
                 Errored -> {
                state.setStatus(TorrentState.Status.Stopped);
                Log.info(Torrent.class, "[{}] Torrent stopped", state.getInfoHash());
                notifiedRef.post(new TorrentNotification.StatusChanged(state.getInfoHash(), TorrentState.Status.Stopped));
            }
            default -> Log.debug(Torrent.class, "[{}] Torrent already stopped", state.getInfoHash());
        }
        return NextState.Receive;
    }

    private NextState handleTerminateCommand(final Mailbox mailbox, final TorrentCommand.Terminate command) {
        Log.debug(Torrent.class, "[{}] Handling terminate command {}", state.getInfoHash(), command);
        notifiedRef.post(new TorrentNotification.Terminated(state.getInfoHash()));
        return NextState.Terminate;
    }

    private NextState handleFailure(final Mailbox mailbox) {
        switch (mailbox.message()) {
            case TorrentCommand command -> {
                Log.error(Torrent.class, "[{}] Failed to handle command", state.getInfoHash(), mailbox.cause());
                notifiedRef.post(new TorrentNotification.Failure(state.getInfoHash(), mailbox.cause()));
            }
            default -> Log.error(Client.class, "Failed to handle message", mailbox.cause());
        }
        return NextState.Terminate;
    }

    private NextState unhandled(final Object message) {
        Log.error(Torrent.class, "[{}] Unhandled message {}", state.getInfoHash(), message);
        return NextState.Receive;
    }
}
