package com.github.jurisliepins.client;

import com.github.jurisliepins.ActorReceiver;
import com.github.jurisliepins.Envelope;
import com.github.jurisliepins.NextState;

public final class Client implements ActorReceiver {

    private final ClientState state;

    public Client(final ClientState state) {
        this.state = state;
    }

    @Override
    public NextState receive(final Envelope envelope) {
        return switch (envelope) {
            case Envelope.Success success -> handle(success);
            case Envelope.Failure failure -> handle(failure);
        };
    }

    private NextState handle(final Envelope.Success envelope) {
        return switch (envelope.message()) {
            case ClientCommand command -> handle(envelope, command);
            case ClientRequest request -> handle(envelope, request);
            default -> unhandled(envelope.message());
        };
    }

    private NextState handle(final Envelope.Success envelope, final ClientCommand command) {
        switch (command) {
            case ClientCommand.Add add -> {
                switch (state.torrents().get(add.torrent())) {
                    case null -> {
                        envelope.system()
                                .spawn(ignored -> NextState.Terminate);
                        envelope.sender()
                                .post(new ClientCommandResult.Success(add.torrent(), "Torrent added"));
                    }
                    case Object torrent -> envelope.sender()
                            .post(new ClientCommandResult.Failure(add.torrent(), "Torrent already exists"));
                }
            }
            case ClientCommand.Remove remove -> {
                switch (state.torrents().get(remove.infoHash())) {
                    case Object torrent -> {
                        envelope.sender()
                                .post(new ClientCommandResult.Success(remove.infoHash(), "Torrent removed"));
                    }
                    case null -> envelope.sender()
                            .post(new ClientCommandResult.Failure(remove.infoHash(), "Torrent not found"));
                }
            }
            case ClientCommand.Start start -> {
                switch (state.torrents().get(start.infoHash())) {
                    case Object torrent -> {
                        envelope.sender()
                                .post(new ClientCommandResult.Success(start.infoHash(), "Torrent started"));
                    }
                    case null -> envelope.sender()
                            .post(new ClientCommandResult.Failure(start.infoHash(), "Torrent not found"));
                }
            }
            case ClientCommand.Stop stop -> {
                switch (state.torrents().get(stop.infoHash())) {
                    case Object torrent -> {
                        envelope.sender()
                                .post(new ClientCommandResult.Success(stop.infoHash(), "Torrent stopped"));
                    }
                    case null -> envelope.sender()
                            .post(new ClientCommandResult.Failure(stop.infoHash(), "Torrent not found"));
                }
            }
        }
        return NextState.Receive;
    }

    private NextState handle(final Envelope.Success envelope, final ClientRequest command) {
        switch (command) {
            case ClientRequest.Get get -> {
            }
        }
        return NextState.Receive;
    }

    private NextState handle(final Envelope.Failure envelope) {
        return NextState.Terminate;
    }

    private NextState unhandled(final Object message) {
        return NextState.Receive;
    }
}
