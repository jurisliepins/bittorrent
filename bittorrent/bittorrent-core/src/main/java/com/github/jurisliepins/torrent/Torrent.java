package com.github.jurisliepins.torrent;

import com.github.jurisliepins.ActorReceiver;
import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.Envelope;
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
    public NextState receive(final Envelope envelope) {
        return switch (envelope) {
            case Envelope.Success success -> handleSuccess(success);
            case Envelope.Failure failure -> handleFailure(failure);
        };
    }

    private NextState handleSuccess(final Envelope.Success envelope) {
        return switch (envelope.message()) {
            case TorrentCommand command -> handleCommand(envelope, command);
            default -> unhandled(envelope.message());
        };
    }

    private NextState handleCommand(final Envelope.Success envelope, final TorrentCommand command) {
        try {
            return switch (command) {
                case TorrentCommand.Start start -> handleStartCommand(envelope, start);
                case TorrentCommand.Stop stop -> handleStopCommand(envelope, stop);
                case TorrentCommand.Terminate terminate -> handleTerminateCommand(envelope, terminate);
            };
        } catch (Exception e) {
            Log.error(Torrent.class, "[{}] Failed to handle command", state.getInfoHash(), e);
            notifiedRef.post(new TorrentNotification.Failure(state.getInfoHash(), "Failed with '%s'".formatted(e.getMessage())));
        }
        return NextState.Receive;
    }

    private NextState handleStartCommand(final Envelope.Success envelope, final TorrentCommand.Start command) {
        Log.debug(Torrent.class, "[{}] Handling start command {}", state.getInfoHash(), command);

        switch (state.getStatus()) {
            case STOPPED -> {
                state.setStatus(TorrentState.Status.STARTED);
                Log.info(Torrent.class, "[{}] Torrent started", state.getInfoHash());
                notifiedRef.post(new TorrentNotification.StatusChanged(state.getInfoHash(), TorrentState.Status.STARTED));
            }
            default -> Log.debug(Torrent.class, "[{}] Torrent already started", state.getInfoHash());
        }

        return NextState.Receive;
    }

    private NextState handleStopCommand(final Envelope.Success envelope, final TorrentCommand.Stop command) {
        Log.debug(Torrent.class, "[{}] Handling stop command {}", state.getInfoHash(), command);

        switch (state.getStatus()) {
            case STARTED,
                 RUNNING,
                 ERRORED -> {
                state.setStatus(TorrentState.Status.STOPPED);
                Log.info(Torrent.class, "[{}] Torrent stopped", state.getInfoHash());
                notifiedRef.post(new TorrentNotification.StatusChanged(state.getInfoHash(), TorrentState.Status.STOPPED));
            }
            default -> Log.debug(Torrent.class, "[{}] Torrent already stopped", state.getInfoHash());
        }

        return NextState.Receive;
    }

    private NextState handleTerminateCommand(final Envelope.Success envelope, final TorrentCommand.Terminate command) {
        Log.debug(Torrent.class, "[{}] Handling terminate command {}", state.getInfoHash(), command);
        notifiedRef.post(new TorrentNotification.Terminated(state.getInfoHash()));
        return NextState.Terminate;
    }

    private NextState handleFailure(final Envelope.Failure envelope) {
        Log.error(Torrent.class, "[{}] Terminating with failure", state.getInfoHash(), envelope.cause());
        notifiedRef.post(new TorrentNotification.Terminated(state.getInfoHash()));
        return NextState.Terminate;
    }

    private NextState unhandled(final Object message) {
        Log.error(Torrent.class, "[{}] Unhandled message {}", state.getInfoHash(), message);
        return NextState.Receive;
    }
}
