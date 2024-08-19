package com.github.jurisliepins.client;

import com.github.jurisliepins.ActorReceiver;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.client.message.ClientCommand;
import com.github.jurisliepins.client.message.ClientCommandResult;
import com.github.jurisliepins.client.message.ClientRequest;
import com.github.jurisliepins.client.message.ClientResponse;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.info.MetaInfo;
import com.github.jurisliepins.log.Log;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;

import java.util.Objects;

public final class ClientActor implements ActorReceiver {

    private final ClientState state;

    public ClientActor(final ClientState state) {
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
            case ClientCommand command -> handleCommand(mailbox, command);
            case ClientRequest request -> handleRequest(mailbox, request);
            case TorrentNotification notification -> handleTorrentNotification(mailbox, notification);
            default -> unhandled(mailbox);
        };
    }

    private NextState handleCommand(final Mailbox.Success mailbox, final ClientCommand command) {
        return switch (command) {
            case ClientCommand.Add add -> handleAddCommand(mailbox, add);
            case ClientCommand.Remove remove -> handleRemoveCommand(mailbox, remove);
            case ClientCommand.Start start -> handleStartCommand(mailbox, start);
            case ClientCommand.Stop stop -> handleStopCommand(mailbox, stop);
        };
    }

    private NextState handleAddCommand(final Mailbox.Success mailbox, final ClientCommand.Add command) {
        Log.debug(ClientActor.class, "Handling add command {}", command);

        switch (MetaInfo.fromBytes(command.metaInfo())) {
            case MetaInfo metaInfo -> {
                switch (state.get(metaInfo.info().hash())) {
                    case ClientStateTorrent ignored -> {
                        Log.info(ClientActor.class, "Torrent '{}' already exists", metaInfo.info().hash());
                        mailbox.reply(new ClientCommandResult.Failure(metaInfo.info().hash(), "Torrent already exists"));
                    }

                    case null -> {
//                        final ClientStateTorrent torrent = new ClientStateTorrent(
//                                mailbox.system().spawn(new Torrent(mailbox.self(), new TorrentState(metaInfo))),
//                                metaInfo);
//                        state.add(torrent);
                        Log.info(ClientActor.class, "Torrent '{}' added", metaInfo.info().hash());
                        mailbox.reply(new ClientCommandResult.Success(metaInfo.info().hash(), "Torrent added"));
                    }
                }
            }
        }
        return NextState.Receive;
    }

    private NextState handleRemoveCommand(final Mailbox.Success mailbox, final ClientCommand.Remove command) {
        Log.debug(ClientActor.class, "Handling remove command {}", command);

        switch (state.remove(command.infoHash())) {
            case ClientStateTorrent torrent -> {
                torrent.getRef().post(new TorrentCommand.Terminate(), mailbox.self());
                Log.info(ClientActor.class, "Removed torrent '{}'", command.infoHash());
                mailbox.reply(new ClientCommandResult.Success(command.infoHash(), "Torrent removed"));
            }

            case null -> {
                Log.info(ClientActor.class, "Torrent '{}' doesn't exist", command.infoHash());
                mailbox.reply(new ClientCommandResult.Failure(command.infoHash(), "Torrent doesn't exist"));
            }
        }
        return NextState.Receive;
    }

    private NextState handleStartCommand(final Mailbox.Success mailbox, final ClientCommand.Start command) {
        Log.debug(ClientActor.class, "Handling start command {}", command);

        switch (state.get(command.infoHash())) {
            case ClientStateTorrent torrent -> {
                torrent.getRef().post(new TorrentCommand.Start(), mailbox.self());
                Log.info(ClientActor.class, "Started torrent '{}'", command.infoHash());
                mailbox.reply(new ClientCommandResult.Success(command.infoHash(), "Torrent started"));
            }

            case null -> {
                Log.info(ClientActor.class, "Torrent '{}' doesn't exist", command.infoHash());
                mailbox.reply(new ClientCommandResult.Failure(command.infoHash(), "Torrent doesn't exist"));
            }
        }
        return NextState.Receive;
    }

    private NextState handleStopCommand(final Mailbox.Success mailbox, final ClientCommand.Stop command) {
        Log.debug(ClientActor.class, "Handling stop command {}", command);

        switch (state.get(command.infoHash())) {
            case ClientStateTorrent torrent -> {
                torrent.getRef().post(new TorrentCommand.Stop(), mailbox.self());
                Log.info(ClientActor.class, "Stopped torrent '{}'", command.infoHash());
                mailbox.reply(new ClientCommandResult.Success(command.infoHash(), "Torrent stopped"));
            }

            case null -> {
                Log.info(ClientActor.class, "Torrent '{}' doesn't exist", command.infoHash());
                mailbox.reply(new ClientCommandResult.Failure(command.infoHash(), "Torrent doesn't exist"));
            }
        }
        return NextState.Receive;
    }

    private NextState handleRequest(final Mailbox.Success mailbox, final ClientRequest request) {
        return switch (request) {
            case ClientRequest.Get get -> handleGetRequest(mailbox, get);
        };
    }

    private NextState handleGetRequest(final Mailbox.Success mailbox, final ClientRequest.Get request) {
        Log.debug(ClientActor.class, "Handling get request {}", request);

        switch (state.get(request.infoHash())) {
            case ClientStateTorrent torrent -> {
                // TODO: !
                Log.info(ClientActor.class, "Found torrent '{}'", request.infoHash());
                mailbox.reply(new ClientResponse.Get(torrent));
            }

            case null -> {
                Log.info(ClientActor.class, "Torrent '{}' doesn't exist", request.infoHash());
                mailbox.reply(new ClientResponse.Failure(request.infoHash(), "Torrent doesn't exist"));
            }
        }
        return NextState.Receive;
    }

    private NextState handleTorrentNotification(final Mailbox.Success mailbox, final TorrentNotification notification) {
        return switch (notification) {
            case TorrentNotification.StatusChanged statusChanged -> handleStatusChangedTorrentNotification(mailbox, statusChanged);
            case TorrentNotification.Terminated terminated -> handleTerminatedTorrentNotification(mailbox, terminated);
            case TorrentNotification.Failure failure -> handleFailureTorrentNotification(mailbox, failure);
        };
    }

    private NextState handleStatusChangedTorrentNotification(final Mailbox.Success mailbox, final TorrentNotification.StatusChanged notification) {
        Log.debug(ClientActor.class, "Handling torrent status changed notification {}", notification);

        switch (state.get(notification.infoHash())) {
            case ClientStateTorrent torrent -> {
                torrent.setStatus(notification.status());
            }

            case null -> {
                Log.debug(ClientActor.class, "Torrent '%s' doesn't exist".formatted(notification.infoHash()));
            }
        }
        return NextState.Receive;
    }

    private NextState handleTerminatedTorrentNotification(final Mailbox.Success mailbox, final TorrentNotification.Terminated notification) {
        Log.debug(ClientActor.class, "Handling torrent terminated notification {}", notification);
        return NextState.Receive;
    }

    private NextState handleFailureTorrentNotification(final Mailbox mailbox, final TorrentNotification.Failure failure) {
        Log.debug(ClientActor.class, "Handling torrent failure notification {}", failure);
        return NextState.Receive;
    }

    private NextState handleFailure(final Mailbox.Failure mailbox) {
        switch (mailbox.message()) {
            case ClientCommand command -> {
                Log.error(ClientActor.class, "Failed to handle command", mailbox.cause());
                mailbox.reply(new ClientCommandResult.Failure(InfoHash.BLANK, "Command failed with '%s'".formatted(mailbox.cause().getMessage())));
            }

            case ClientRequest request -> {
                Log.error(ClientActor.class, "Failed to handle request", mailbox.cause());
                mailbox.reply(new ClientResponse.Failure(InfoHash.BLANK, "Request failed with '%s'".formatted(mailbox.cause().getMessage())));
            }

            case TorrentNotification notification -> {
                Log.error(ClientActor.class, "Failed to handle torrent notification", mailbox.cause());
            }

            default -> Log.error(ClientActor.class, "Failed to handle message", mailbox.cause());
        }
        return NextState.Receive;
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        Log.error(ClientActor.class, "Unhandled message {}", mailbox.message());
        return NextState.Receive;
    }
}
