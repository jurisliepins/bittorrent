package com.github.jurisliepins.torrent;

import com.github.jurisliepins.ActorReceiver;
import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.client.ClientActor;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.log.Log;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import com.github.jurisliepins.types.StatusType;

import java.util.Objects;

public final class TorrentActor implements ActorReceiver {

    private final ActorRef notifiedRef;

    private final TorrentState state;

    public TorrentActor(final ActorRef notifiedRef, final TorrentState state) {
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
        Log.debug(TorrentActor.class, "[{}] Handling start command {}", state.getInfoHash(), command);

        switch (state.getStatus()) {
            case Stopped -> {
                state.setStatus(StatusType.Started);
                Log.info(TorrentActor.class, "[{}] Torrent started", state.getInfoHash());
                notifiedRef.post(new TorrentNotification.StatusChanged(state.getInfoHash(), StatusType.Started));
            }

            case Started,
                 Running,
                 Errored -> {
                Log.debug(TorrentActor.class, "[{}] Torrent already started", state.getInfoHash());
            }
        }
        return NextState.Receive;
    }

    private NextState handleStopCommand(final Mailbox.Success mailbox, final TorrentCommand.Stop command) {
        Log.debug(TorrentActor.class, "[{}] Handling stop command {}", state.getInfoHash(), command);

        switch (state.getStatus()) {
            case Started,
                 Running,
                 Errored -> {
                state.setStatus(StatusType.Stopped);
                Log.info(TorrentActor.class, "[{}] Torrent stopped", state.getInfoHash());
                notifiedRef.post(new TorrentNotification.StatusChanged(state.getInfoHash(), StatusType.Stopped));
            }

            case Stopped -> {
                Log.debug(TorrentActor.class, "[{}] Torrent already stopped", state.getInfoHash());
            }
        }
        return NextState.Receive;
    }

    private NextState handleTerminateCommand(final Mailbox.Success mailbox, final TorrentCommand.Terminate command) {
        Log.debug(TorrentActor.class, "[{}] Handling terminate command {}", state.getInfoHash(), command);
        notifiedRef.post(new TorrentNotification.Terminated(state.getInfoHash()));
        return NextState.Terminate;
    }

    private NextState handleFailure(final Mailbox.Failure mailbox) {
        switch (mailbox.message()) {
            case TorrentCommand command -> {
                Log.error(TorrentActor.class, "[{}] Failed to handle command", state.getInfoHash(), mailbox.cause());
                notifiedRef.post(new TorrentNotification.Failure(state.getInfoHash(), mailbox.cause()));
            }

            default -> Log.error(ClientActor.class, "Failed to handle message", mailbox.cause());
        }
        return NextState.Terminate;
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        Log.error(TorrentActor.class, "[{}] Unhandled message {}", state.getInfoHash(), mailbox.message());
        return NextState.Receive;
    }
}
