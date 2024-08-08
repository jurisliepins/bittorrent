package com.github.jurisliepins.client;

import com.github.jurisliepins.ActorReceiver;
import com.github.jurisliepins.Envelope;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.client.message.ClientCommand;
import com.github.jurisliepins.client.message.ClientCommandResult;
import com.github.jurisliepins.client.message.ClientRequest;
import com.github.jurisliepins.client.message.ClientResponse;
import com.github.jurisliepins.client.state.ClientState;
import com.github.jurisliepins.client.state.ClientStateTorrent;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.info.MetaInfo;
import com.github.jurisliepins.log.Log;
import com.github.jurisliepins.torrent.Torrent;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import com.github.jurisliepins.torrent.state.TorrentState;

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
            case TorrentNotification notification -> handleTorrentNotification(envelope, notification);
            default -> unhandled(envelope.message());
        };
    }

    private NextState handleCommand(final Envelope.Success envelope, final ClientCommand command) {
        try {
            return switch (command) {
                case ClientCommand.Add add -> handleAddCommand(envelope, add);
                case ClientCommand.Remove remove -> handleRemoveCommand(envelope, remove);
                case ClientCommand.Start start -> handleStartCommand(envelope, start);
                case ClientCommand.Stop stop -> handleStopCommand(envelope, stop);
            };
        } catch (Exception e) {
            Log.error(Client.class, "Failed to handle command", e);
            envelope.reply(new ClientCommandResult.Failure(InfoHash.BLANK, "Failed with '%s'".formatted(e.getMessage())));
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
                        envelope.reply(new ClientCommandResult.Failure(metaInfo.info().hash(), "Torrent already exists"));
                    }

                    case null -> {
                        final ClientStateTorrent torrent = new ClientStateTorrent(
                                envelope.system().spawn(new Torrent(envelope.self(), new TorrentState(metaInfo))),
                                metaInfo);
                        state.add(torrent);
                        Log.info(Client.class, "Torrent '{}' added", metaInfo.info().hash());
                        envelope.reply(new ClientCommandResult.Success(metaInfo.info().hash(), "Torrent added"));
                    }
                }
            }
        }
        return NextState.Receive;
    }

    private NextState handleRemoveCommand(final Envelope.Success envelope, final ClientCommand.Remove command) {
        Log.debug(Client.class, "Handling remove command {}", command);

        switch (state.remove(command.infoHash())) {
            case ClientStateTorrent torrent -> {
                torrent.getRef().post(new TorrentCommand.Terminate(), envelope.self());
                Log.info(Client.class, "Removed torrent '{}'", command.infoHash());
                envelope.reply(new ClientCommandResult.Success(command.infoHash(), "Torrent removed"));
            }

            case null -> {
                Log.info(Client.class, "Torrent '{}' doesn't exist", command.infoHash());
                envelope.reply(new ClientCommandResult.Failure(command.infoHash(), "Torrent doesn't exist"));
            }
        }
        return NextState.Receive;
    }

    private NextState handleStartCommand(final Envelope.Success envelope, final ClientCommand.Start command) {
        Log.debug(Client.class, "Handling start command {}", command);

        switch (state.get(command.infoHash())) {
            case ClientStateTorrent torrent -> {
                torrent.getRef().post(new TorrentCommand.Start(), envelope.self());
                Log.info(Client.class, "Started torrent '{}'", command.infoHash());
                envelope.reply(new ClientCommandResult.Success(command.infoHash(), "Torrent started"));
            }

            case null -> {
                Log.info(Client.class, "Torrent '{}' doesn't exist", command.infoHash());
                envelope.reply(new ClientCommandResult.Failure(command.infoHash(), "Torrent doesn't exist"));
            }
        }
        return NextState.Receive;
    }

    private NextState handleStopCommand(final Envelope.Success envelope, final ClientCommand.Stop command) {
        Log.debug(Client.class, "Handling stop command {}", command);

        switch (state.get(command.infoHash())) {
            case ClientStateTorrent torrent -> {
                torrent.getRef().post(new TorrentCommand.Stop(), envelope.self());
                Log.info(Client.class, "Stopped torrent '{}'", command.infoHash());
                envelope.reply(new ClientCommandResult.Success(command.infoHash(), "Torrent stopped"));
            }

            case null -> {
                Log.info(Client.class, "Torrent '{}' doesn't exist", command.infoHash());
                envelope.reply(new ClientCommandResult.Failure(command.infoHash(), "Torrent doesn't exist"));
            }
        }
        return NextState.Receive;
    }

    private NextState handleRequest(final Envelope.Success envelope, final ClientRequest request) {
        try {
            return switch (request) {
                case ClientRequest.Get get -> handleGetRequest(envelope, get);
            };
        } catch (Exception e) {
            Log.error(Client.class, "Failed to handle request", e);
            envelope.reply(new ClientResponse.Failure(InfoHash.BLANK, "Failed with '%s'".formatted(e.getMessage())));
        }
        return NextState.Receive;
    }

    private NextState handleGetRequest(final Envelope.Success envelope, final ClientRequest.Get request) {
        Log.debug(Client.class, "Handling get request {}", request);

        switch (state.get(request.infoHash())) {
            case ClientStateTorrent torrent -> {
                Log.info(Client.class, "Found torrent '{}'", request.infoHash());
                envelope.reply(new ClientResponse.Get(torrent));
            }

            case null -> {
                Log.info(Client.class, "Torrent '{}' doesn't exist", request.infoHash());
                envelope.reply(new ClientResponse.Failure(request.infoHash(), "Torrent doesn't exist"));
            }
        }
        return NextState.Receive;
    }

    private NextState handleTorrentNotification(final Envelope.Success envelope, final TorrentNotification notification) {
        try {
            return switch (notification) {
                case TorrentNotification.StatusChanged statusChanged -> handleStatusChangedTorrentNotification(envelope, statusChanged);
                case TorrentNotification.Terminated terminated -> handleTerminatedTorrentNotification(envelope, terminated);
                case TorrentNotification.Failure failure -> handleFailureTorrentNotification(envelope, failure);
            };
        } catch (Exception e) {
            Log.error(Client.class, "Failed to handle torrent notification", e);
        }
        return NextState.Receive;
    }

    private NextState handleStatusChangedTorrentNotification(final Envelope.Success envelope, final TorrentNotification.StatusChanged notification) {
        Log.debug(Client.class, "Handling torrent status changed notification {}", notification);
        return NextState.Receive;
    }

    private NextState handleTerminatedTorrentNotification(final Envelope.Success envelope, final TorrentNotification.Terminated notification) {
        Log.debug(Client.class, "Handling torrent terminated notification {}", notification);
        return NextState.Receive;
    }

    private NextState handleFailureTorrentNotification(final Envelope.Success envelope, final TorrentNotification.Failure failure) {
        Log.debug(Client.class, "Handling torrent failure notification {}", failure);
        return NextState.Receive;
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
