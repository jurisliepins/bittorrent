package com.github.jurisliepins.client;

import com.github.jurisliepins.MailboxReceiver;
import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.client.message.ClientCommand;
import com.github.jurisliepins.client.message.ClientCommandResult;
import com.github.jurisliepins.client.message.ClientRequest;
import com.github.jurisliepins.client.message.ClientResponse;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.info.MetaInfo;
import com.github.jurisliepins.log.Log;
import com.github.jurisliepins.torrent.TorrentMailboxReceiver;
import com.github.jurisliepins.torrent.TorrentState;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import com.github.jurisliepins.types.StatusType;

import java.util.List;
import java.util.Objects;

public final class ClientMailboxReceiver implements MailboxReceiver {

    private final ClientState state;

    public ClientMailboxReceiver(final ClientState state) {
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
        Log.debug(ClientMailboxReceiver.class, "Handling add command {}", command);

        switch (MetaInfo.fromBytes(command.metaInfo())) {
            case MetaInfo metaInfo -> {
                switch (state.get(metaInfo.info().hash())) {
                    case ClientState.Torrent ignored -> {
                        Log.info(ClientMailboxReceiver.class, "Torrent '{}' already exists", metaInfo.info().hash());
                        mailbox.reply(new ClientCommandResult.Failure(metaInfo.info().hash(), "Torrent already exists"));
                    }

                    case null -> {
                        final ActorRef torrentRef = mailbox.system()
                                .spawn(new TorrentMailboxReceiver(mailbox.self(),
                                                                  TorrentState.builder()
                                                                .status(StatusType.Stopped)
                                                                .infoHash(metaInfo.info().hash())
                                                                .peerId(new Object())
                                                                .bitfield(new Bitfield(metaInfo.info().pieces().length))
                                                                .pieceLength(metaInfo.info().pieceLength())
                                                                .pieces(List.of())
                                                                .files(List.of())
                                                                .name(metaInfo.info().name())
                                                                .length(metaInfo.info().length())
                                                                .downloaded(0L)
                                                                .uploaded(0L)
                                                                .left(metaInfo.info().length())
                                                                .announce(metaInfo.announce())
                                                                .downloadRate(0.0)
                                                                .uploadRate(0.0)
                                                                .build()));
                        state.add(ClientState.Torrent.builder()
                                          .ref(torrentRef)
                                          .status(StatusType.Stopped)
                                          .infoHash(metaInfo.info().hash())
                                          .peerId(new Object())
                                          .bitfield(new Bitfield(metaInfo.info().pieces().length))
                                          .pieceLength(metaInfo.info().pieceLength())
                                          .name(metaInfo.info().name())
                                          .length(metaInfo.info().length())
                                          .downloaded(0L)
                                          .uploaded(0L)
                                          .left(metaInfo.info().length())
                                          .downloadRate(0.0)
                                          .uploadRate(0.0)
                                          .build());
                        Log.info(ClientMailboxReceiver.class, "Torrent '{}' added", metaInfo.info().hash());
                        mailbox.reply(new ClientCommandResult.Success(metaInfo.info().hash(), "Torrent added"));
                    }
                }
            }
        }
        return NextState.Receive;
    }

    private NextState handleRemoveCommand(final Mailbox.Success mailbox, final ClientCommand.Remove command) {
        Log.debug(ClientMailboxReceiver.class, "Handling remove command {}", command);

        switch (state.remove(command.infoHash())) {
            case ClientState.Torrent torrent -> {
                torrent.getRef().post(new TorrentCommand.Terminate(), mailbox.self());
                Log.info(ClientMailboxReceiver.class, "Removed torrent '{}'", command.infoHash());
                mailbox.reply(new ClientCommandResult.Success(command.infoHash(), "Torrent removed"));
            }

            case null -> {
                Log.info(ClientMailboxReceiver.class, "Torrent '{}' doesn't exist", command.infoHash());
                mailbox.reply(new ClientCommandResult.Failure(command.infoHash(), "Torrent doesn't exist"));
            }
        }
        return NextState.Receive;
    }

    private NextState handleStartCommand(final Mailbox.Success mailbox, final ClientCommand.Start command) {
        Log.debug(ClientMailboxReceiver.class, "Handling start command {}", command);

        switch (state.get(command.infoHash())) {
            case ClientState.Torrent torrent -> {
                switch (torrent.getStatus()) {
                    case Stopped -> {
                        torrent.getRef().post(new TorrentCommand.Start(), mailbox.self());
                        Log.info(ClientMailboxReceiver.class, "Started torrent '{}'", torrent.getInfoHash());
                        mailbox.reply(new ClientCommandResult.Success(torrent.getInfoHash(), "Torrent started"));
                    }

                    case Started,
                         Running,
                         Errored -> {
                        Log.info(ClientMailboxReceiver.class, "Torrent '{}' already started", torrent.getInfoHash());
                        mailbox.reply(new ClientCommandResult.Failure(torrent.getInfoHash(), "Torrent already started"));
                    }

                    default -> throw new IllegalStateException("Unexpected status value '%s'".formatted(torrent.getStatus()));
                }
            }

            case null -> {
                Log.info(ClientMailboxReceiver.class, "Torrent '{}' doesn't exist", command.infoHash());
                mailbox.reply(new ClientCommandResult.Failure(command.infoHash(), "Torrent doesn't exist"));
            }
        }
        return NextState.Receive;
    }

    private NextState handleStopCommand(final Mailbox.Success mailbox, final ClientCommand.Stop command) {
        Log.debug(ClientMailboxReceiver.class, "Handling stop command {}", command);

        switch (state.get(command.infoHash())) {
            case ClientState.Torrent torrent -> {
                switch (torrent.getStatus()) {
                    case Started,
                         Running,
                         Errored -> {
                        torrent.getRef().post(new TorrentCommand.Stop(), mailbox.self());
                        Log.info(ClientMailboxReceiver.class, "Stopped torrent '{}'", command.infoHash());
                        mailbox.reply(new ClientCommandResult.Success(command.infoHash(), "Torrent stopped"));
                    }

                    case Stopped -> {
                        Log.info(ClientMailboxReceiver.class, "Torrent '{}' already stopped", torrent.getInfoHash());
                        mailbox.reply(new ClientCommandResult.Failure(torrent.getInfoHash(), "Torrent already stopped"));
                    }

                    default -> throw new IllegalStateException("Unexpected status value '%s'".formatted(torrent.getStatus()));
                }
            }

            case null -> {
                Log.info(ClientMailboxReceiver.class, "Torrent '{}' doesn't exist", command.infoHash());
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
        Log.debug(ClientMailboxReceiver.class, "Handling get request {}", request);

        switch (state.get(request.infoHash())) {
            case ClientState.Torrent torrent -> {
                Log.info(ClientMailboxReceiver.class, "Found torrent '{}'", request.infoHash());
                final ClientResponse.Torrent responseTorrent = new ClientResponse.Torrent(
                        torrent.getStatus(),
                        torrent.getInfoHash(),
                        torrent.getPeerId(),
                        torrent.getBitfield(),
                        torrent.getName(),
                        torrent.getLength(),
                        torrent.getDownloaded(),
                        torrent.getUploaded(),
                        torrent.getLeft(),
                        torrent.getDownloadRate(),
                        torrent.getUploadRate());
                mailbox.reply(new ClientResponse.Get(responseTorrent));
            }

            case null -> {
                Log.info(ClientMailboxReceiver.class, "Torrent '{}' doesn't exist", request.infoHash());
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
        Log.debug(ClientMailboxReceiver.class, "Handling torrent status changed notification {}", notification);

        switch (state.get(notification.infoHash())) {
            case ClientState.Torrent torrent -> {
                torrent.setStatus(notification.status());
            }

            case null -> {
                Log.debug(ClientMailboxReceiver.class, "Torrent '%s' doesn't exist".formatted(notification.infoHash()));
            }
        }
        return NextState.Receive;
    }

    private NextState handleTerminatedTorrentNotification(final Mailbox.Success mailbox, final TorrentNotification.Terminated notification) {
        Log.debug(ClientMailboxReceiver.class, "Handling torrent terminated notification {}", notification);
        return NextState.Receive;
    }

    private NextState handleFailureTorrentNotification(final Mailbox mailbox, final TorrentNotification.Failure failure) {
        Log.debug(ClientMailboxReceiver.class, "Handling torrent failure notification {}", failure);
        return NextState.Receive;
    }

    private NextState handleFailure(final Mailbox.Failure mailbox) {
        switch (mailbox.message()) {
            case ClientCommand command -> {
                Log.error(ClientMailboxReceiver.class, "Failed to handle command", mailbox.cause());
                mailbox.reply(new ClientCommandResult.Failure(InfoHash.BLANK, "Command failed with '%s'".formatted(mailbox.cause().getMessage())));
            }

            case ClientRequest request -> {
                Log.error(ClientMailboxReceiver.class, "Failed to handle request", mailbox.cause());
                mailbox.reply(new ClientResponse.Failure(InfoHash.BLANK, "Request failed with '%s'".formatted(mailbox.cause().getMessage())));
            }

            case TorrentNotification notification -> {
                Log.error(ClientMailboxReceiver.class, "Failed to handle torrent notification", mailbox.cause());
            }

            default -> Log.error(ClientMailboxReceiver.class, "Failed to handle message", mailbox.cause());
        }
        return NextState.Receive;
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        Log.error(ClientMailboxReceiver.class, "Unhandled message {}", mailbox.message());
        return NextState.Receive;
    }
}