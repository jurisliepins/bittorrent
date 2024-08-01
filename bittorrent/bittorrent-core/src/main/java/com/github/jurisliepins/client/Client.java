package com.github.jurisliepins.client;

import com.github.jurisliepins.ActorReceiver;
import com.github.jurisliepins.Envelope;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.info.MetaInfo;
import com.github.jurisliepins.log.Log;

import java.util.Objects;

public final class Client implements ActorReceiver {

    private final ClientState state;

    public Client(final ClientState state) {
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
            case ClientCommand command -> handleCommand(envelope, command);
            case ClientRequest request -> handleRequest(envelope, request);
            default -> unhandled(envelope.message());
        };
    }

    private NextState handleCommand(final Envelope.Success envelope, final ClientCommand command) {
        try {
            return switch (command) {
                case ClientCommand.Add addCommand -> handleAddCommand(envelope, addCommand);
                case ClientCommand.Remove removeCommand -> handleRemoveCommand(envelope, removeCommand);
                case ClientCommand.Start startCommand -> handleStartCommand(envelope, startCommand);
                case ClientCommand.Stop stopCommand -> handleStopCommand(envelope, stopCommand);
            };
        } catch (Exception e) {
            Log.error(Client.class, "Failed to handle command", e);
            envelope.sender()
                    .post(new ClientCommandResult.Failure(
                            InfoHash.BLANK,
                            "Failed with '%s'".formatted(e.getMessage())));
        }
        return NextState.Receive;
    }

    private NextState handleAddCommand(final Envelope.Success envelope, final ClientCommand.Add command) {
        Log.debug(Client.class, "Handling add command {}", command);

        switch (MetaInfo.fromBytes(command.metaInfo())) {
            case MetaInfo metaInfo -> {
                switch (state.get(metaInfo.info().hash())) {
                    case ClientStateTorrent ignored -> {
                        Log.info(Client.class, "Torrent '{}' already exists", metaInfo.info().hash());
                        envelope.sender()
                                .post(new ClientCommandResult.Failure(
                                        metaInfo.info().hash(), "Torrent already exists"));
                    }

                    case null -> {
                        state.add(new ClientStateTorrent());
                        Log.info(Client.class, "Torrent '{}' added", metaInfo.info().hash());
                        envelope.sender()
                                .post(new ClientCommandResult.Success(
                                        metaInfo.info().hash(), "Torrent added"));
                    }
                }
            }
        }
        return NextState.Receive;
    }

    private NextState handleRemoveCommand(final Envelope.Success envelope, final ClientCommand.Remove command) {
        Log.debug(Client.class, "Handling remove command {}", command);

        switch (state.get(command.infoHash())) {
            case ClientStateTorrent ignored -> {
                state.remove(command.infoHash());
                Log.info(Client.class, "Removed torrent '{}'", command.infoHash());
                envelope.sender()
                        .post(new ClientCommandResult.Success(command.infoHash(), "Torrent removed"));
            }

            case null -> {
                Log.info(Client.class, "Torrent '{}' doesn't exist", command.infoHash());
                envelope.sender()
                        .post(new ClientCommandResult.Failure(command.infoHash(), "Torrent doesn't exist"));
            }
        }
        return NextState.Receive;
    }

    private NextState handleStartCommand(final Envelope.Success envelope, final ClientCommand.Start command) {
        return NextState.Receive;
    }

    private NextState handleStopCommand(final Envelope.Success envelope, final ClientCommand.Stop command) {
        return NextState.Receive;
    }

    private NextState handleRequest(final Envelope.Success envelope, final ClientRequest request) {
        try {
            return switch (request) {
                case ClientRequest.Get getRequest -> handleGetRequest(envelope, getRequest);
            };
        } catch (Exception e) {
            Log.error(Client.class, "Failed to handle request", e);
            envelope.sender()
                    .post(new ClientResponse.Failure(InfoHash.BLANK, "Failed with '%s'".formatted(e.getMessage())));
        }
        return NextState.Receive;
    }

    private NextState handleGetRequest(final Envelope.Success envelope, final ClientRequest.Get request) {
        Log.debug(Client.class, "Handling get request {}", request);

        switch (state.get(request.infoHash())) {
            case ClientStateTorrent torrent -> {
                Log.info(Client.class, "Found torrent '{}'", request.infoHash());
                envelope.sender()
                        .post(new ClientResponse.Get(torrent));
            }

            case null -> {
                Log.info(Client.class, "Torrent '{}' doesn't exist", request.infoHash());
                envelope.sender()
                        .post(new ClientResponse.Failure(request.infoHash(), "Torrent doesn't exist"));
            }
        }
        return NextState.Receive;
    }

    private NextState handleFailure(final Envelope.Failure envelope) {
        Log.error(Client.class, "Terminating with failure", envelope.cause());
        return NextState.Terminate;
    }

    private NextState unhandled(final Object message) {
        Log.info(Client.class, "Unhandled message {}", message);
        return NextState.Receive;
    }
}
