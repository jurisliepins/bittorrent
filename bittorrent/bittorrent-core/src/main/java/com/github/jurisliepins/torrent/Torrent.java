package com.github.jurisliepins.torrent;

import com.github.jurisliepins.ActorReceiver;
import com.github.jurisliepins.Envelope;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.client.Client;
import com.github.jurisliepins.log.Log;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentCommandResult;
import com.github.jurisliepins.torrent.state.TorrentState;

import java.util.Objects;

public final class Torrent implements ActorReceiver {

    private final TorrentState state;

    public Torrent(final TorrentState state) {
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
                case TorrentCommand.Start startCommand -> handleStartCommand(envelope, startCommand);
                case TorrentCommand.Stop stopCommand -> handleStopCommand(envelope, stopCommand);
                case TorrentCommand.Terminate terminateCommand -> handleTerminateCommand(envelope, terminateCommand);
            };
        } catch (Exception e) {
            Log.error(Client.class, "Failed to handle command", e);
            envelope.reply(new TorrentCommandResult.Failure(
                    state.getInfoHash(), "Failed with '%s'".formatted(e.getMessage())));
        }
        return NextState.Receive;
    }

    private NextState handleStartCommand(final Envelope.Success envelope, final TorrentCommand.Start command) {
        Log.debug(Torrent.class, "Handling start command {}", command);
        return NextState.Receive;
    }

    private NextState handleStopCommand(final Envelope.Success envelope, final TorrentCommand.Stop command) {
        Log.debug(Torrent.class, "Handling stop command {}", command);
        return NextState.Receive;
    }

    private NextState handleTerminateCommand(final Envelope.Success envelope, final TorrentCommand.Terminate command) {
        Log.debug(Torrent.class, "Handling terminate command {}", command);
        return NextState.Terminate;
    }

    private NextState handleFailure(final Envelope.Failure envelope) {
        Log.error(Client.class, "Terminating with failure", envelope.cause());
        return NextState.Terminate;
    }

    private NextState unhandled(final Object message) {
        Log.error(Client.class, "Unhandled message {}", message);
        return NextState.Receive;
    }
}
