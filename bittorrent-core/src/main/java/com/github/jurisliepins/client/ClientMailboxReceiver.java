package com.github.jurisliepins.client;

import com.github.jurisliepins.CoreMailboxStateLoggingReceiver;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.AnnouncerMailboxReceiver;
import com.github.jurisliepins.announcer.AnnouncerState;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.client.message.ClientCommand;
import com.github.jurisliepins.client.message.ClientCommandResult;
import com.github.jurisliepins.client.message.ClientRequest;
import com.github.jurisliepins.client.message.ClientResponse;
import com.github.jurisliepins.config.Config;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.info.MetaInfo;
import com.github.jurisliepins.torrent.TorrentMailboxReceiver;
import com.github.jurisliepins.torrent.TorrentState;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import com.github.jurisliepins.types.StatusType;
import lombok.NonNull;

import java.util.List;

public final class ClientMailboxReceiver extends CoreMailboxStateLoggingReceiver<ClientState> {
    private final Config config;

    public ClientMailboxReceiver(
            @NonNull final Config config,
            @NonNull final ClientState state) {
        super(state);
        this.config = config;
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
            case AnnouncerNotification notification -> handleAnnouncerNotification(mailbox, notification);
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
        switch (MetaInfo.fromBytes(command.metaInfo())) {
            case MetaInfo metaInfo -> {
                switch (state().getTorrents().get(metaInfo.info().hash())) {
                    case ClientState.Torrent ignored ->
                            mailbox.reply(new ClientCommandResult.Failure(metaInfo.info().hash(), "Torrent already exists"));

                    case null -> {
                        var announcerRef = mailbox.system()
                                .spawn(new AnnouncerMailboxReceiver(config, mailbox.self(), AnnouncerState.builder()
                                        .status(StatusType.Stopped)
                                        .infoHash(metaInfo.info().hash())
                                        .selfPeerId(state().getSelfPeerId())
                                        .announce(metaInfo.announce())
                                        .peerCount(state().getSettings().getPeerCount())
                                        .port(state().getSettings().getPort())
                                        .downloaded(0L)
                                        .uploaded(0L)
                                        .left(metaInfo.info().length())
                                        .build()));
                        var torrentRef = mailbox.system()
                                .spawn(new TorrentMailboxReceiver(config, announcerRef, mailbox.self(), TorrentState.builder()
                                        .status(StatusType.Stopped)
                                        .infoHash(metaInfo.info().hash())
                                        .selfPeerId(state().getSelfPeerId())
                                        .bitfield(new Bitfield())
                                        .pieces(List.of())
                                        .files(List.of())
                                        .name(metaInfo.info().name())
                                        .announce(metaInfo.announce())
                                        .pieceLength(metaInfo.info().pieceLength())
                                        .length(metaInfo.info().length())
                                        .downloaded(0L)
                                        .uploaded(0L)
                                        .left(metaInfo.info().length())
                                        .downloadRate(0.0)
                                        .uploadRate(0.0)
                                        .build()));
                        state().getTorrents()
                                .add(ClientState.Torrent.builder()
                                             .ref(torrentRef)
                                             .status(StatusType.Stopped)
                                             .infoHash(metaInfo.info().hash())
                                             .selfPeerId(state().getSelfPeerId())
                                             .bitfield(new Bitfield())
                                             .pieceLength(metaInfo.info().pieceLength())
                                             .name(metaInfo.info().name())
                                             .length(metaInfo.info().length())
                                             .downloaded(0L)
                                             .uploaded(0L)
                                             .left(metaInfo.info().length())
                                             .downloadRate(0.0)
                                             .uploadRate(0.0)
                                             .build());
                        mailbox.reply(new ClientCommandResult.Success(metaInfo.info().hash(), "Torrent added"));
                    }
                }
            }
        }
        return receiveNext();
    }

    private NextState handleRemoveCommand(final Mailbox.Success mailbox, final ClientCommand.Remove command) {
        switch (state().getTorrents().remove(command.infoHash())) {
            case ClientState.Torrent torrent -> {
                torrent.getRef().post(TorrentCommand.Terminate.INSTANCE, mailbox.self());
                mailbox.reply(new ClientCommandResult.Success(command.infoHash(), "Torrent removed"));
            }

            case null -> mailbox.reply(new ClientCommandResult.Failure(command.infoHash(), "Torrent doesn't exist"));
        }
        return receiveNext();
    }

    private NextState handleStartCommand(final Mailbox.Success mailbox, final ClientCommand.Start command) {
        switch (state().getTorrents().get(command.infoHash())) {
            case ClientState.Torrent torrent -> {
                switch (torrent.getStatus()) {
                    case Stopped -> {
                        torrent.getRef().post(TorrentCommand.Start.INSTANCE, mailbox.self());
                        mailbox.reply(new ClientCommandResult.Success(torrent.getInfoHash(), "Torrent started"));
                    }

                    default -> mailbox.reply(new ClientCommandResult.Failure(torrent.getInfoHash(), "Torrent already started"));
                }
            }

            case null -> mailbox.reply(new ClientCommandResult.Failure(command.infoHash(), "Torrent doesn't exist"));
        }
        return receiveNext();
    }

    private NextState handleStopCommand(final Mailbox.Success mailbox, final ClientCommand.Stop command) {
        switch (state().getTorrents().get(command.infoHash())) {
            case ClientState.Torrent torrent -> {
                switch (torrent.getStatus()) {
                    case Started -> {
                        torrent.getRef().post(TorrentCommand.Stop.INSTANCE, mailbox.self());
                        mailbox.reply(new ClientCommandResult.Success(command.infoHash(), "Torrent stopped"));
                    }

                    default -> mailbox.reply(new ClientCommandResult.Failure(torrent.getInfoHash(), "Torrent already stopped"));
                }
            }

            case null -> mailbox.reply(new ClientCommandResult.Failure(command.infoHash(), "Torrent doesn't exist"));
        }
        return receiveNext();
    }

    private NextState handleRequest(final Mailbox.Success mailbox, final ClientRequest request) {
        return switch (request) {
            case ClientRequest.Get get -> handleGetRequest(mailbox, get);
        };
    }

    private NextState handleGetRequest(final Mailbox.Success mailbox, final ClientRequest.Get request) {
        switch (state().getTorrents().get(request.infoHash())) {
            case ClientState.Torrent torrent -> mailbox.reply(new ClientResponse.Get(ClientResponse.Torrent.of(torrent)));

            case null -> mailbox.reply(new ClientResponse.Failure(request.infoHash(), "Torrent doesn't exist"));
        }
        return receiveNext();
    }

    private NextState handleTorrentNotification(final Mailbox.Success mailbox, final TorrentNotification notification) {
        return switch (notification) {
            case TorrentNotification.StatusChanged statusChanged -> handleStatusChangedTorrentNotification(mailbox, statusChanged);
            case TorrentNotification.Terminated terminated -> handleTerminatedTorrentNotification(mailbox, terminated);
            case TorrentNotification.Failure failure -> handleFailureTorrentNotification(mailbox, failure);
        };
    }

    private NextState handleStatusChangedTorrentNotification(
            final Mailbox.Success mailbox,
            final TorrentNotification.StatusChanged notification) {
        switch (state().getTorrents().get(notification.infoHash())) {
            case ClientState.Torrent torrent -> torrent.setStatus(notification.status());

            case null -> logger().error("Torrent notification for non-existent torrent '{}'", notification.infoHash());
        }
        return receiveNext();
    }

    private NextState handleTerminatedTorrentNotification(
            final Mailbox.Success mailbox,
            final TorrentNotification.Terminated notification) {
        switch (state().getTorrents().remove(notification.infoHash())) {
            case ClientState.Torrent torrent -> { /* Ignored. */ }

            case null -> logger().error("Torrent notification for non-existent torrent '{}'", notification.infoHash());
        }
        return receiveNext();
    }

    private NextState handleFailureTorrentNotification(
            final Mailbox mailbox,
            final TorrentNotification.Failure failure) {
        logger().error("Torrent {} failed", failure.infoHash(), failure.cause());
        return receiveNext();
    }

    private NextState handleAnnouncerNotification(final Mailbox.Success mailbox, final AnnouncerNotification notification) {
        switch (state().getTorrents().get(notification.infoHash())) {
            case ClientState.Torrent torrent -> torrent.getRef().post(notification, mailbox.sender());

            case null -> { /* Ignored. */ }
        }
        return receiveNext();
    }

    private NextState handleFailure(final Mailbox.Failure mailbox) {
        switch (mailbox.message()) {
            case ClientCommand command -> {
                logger().error("Failed to handle command", mailbox.cause());
                mailbox.reply(new ClientCommandResult.Failure(
                        InfoHash.BLANK,
                        "Command failed with '%s'".formatted(mailbox.cause().getMessage())));
            }

            case ClientRequest request -> {
                logger().error("Failed to handle request", mailbox.cause());
                mailbox.reply(new ClientResponse.Failure(
                        InfoHash.BLANK,
                        "Request failed with '%s'".formatted(mailbox.cause().getMessage())));
            }

            case TorrentNotification notification -> logger().error("Failed to handle torrent notification", mailbox.cause());

            default -> logger().error("Failed", mailbox.cause());
        }
        return receiveNext();
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        logger().error("Unhandled message {}", mailbox.message());
        return receiveNext();
    }
}
