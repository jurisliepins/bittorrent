package com.github.jurisliepins.client;

import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.MailboxReceiver;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.AnnouncerMailboxReceiver;
import com.github.jurisliepins.announcer.AnnouncerState;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.client.message.ClientCommand;
import com.github.jurisliepins.client.message.ClientCommandResult;
import com.github.jurisliepins.client.message.ClientRequest;
import com.github.jurisliepins.client.message.ClientResponse;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.info.MetaInfo;
import com.github.jurisliepins.torrent.TorrentMailboxReceiver;
import com.github.jurisliepins.torrent.TorrentState;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import com.github.jurisliepins.types.StatusType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ClientMailboxReceiver implements MailboxReceiver {
    private final Context context;
    private final ClientState state;

    public ClientMailboxReceiver(
            @NonNull final Context context,
            @NonNull final ClientState state) {
        this.context = context;
        this.state = state;
    }

    @Override
    public NextState receive(final Mailbox mailbox) {
        return switch (mailbox) {
            case Mailbox.Success m -> handle(m);
            case Mailbox.Failure m -> handle(m);
        };
    }

    private NextState handle(final Mailbox.Success mailbox) {
        return switch (mailbox.message()) {
            case ClientCommand command -> handle(mailbox, command);
            case ClientRequest request -> handle(mailbox, request);
            case TorrentNotification notification -> handle(mailbox, notification);
            case AnnouncerNotification notification -> handle(mailbox, notification);
            default -> unhandled(mailbox);
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientCommand command) {
        return switch (command) {
            case ClientCommand.Add add -> handle(mailbox, add);
            case ClientCommand.Remove remove -> handle(mailbox, remove);
            case ClientCommand.Start start -> handle(mailbox, start);
            case ClientCommand.Stop stop -> handle(mailbox, stop);
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientCommand.Add command) {
        switch (MetaInfo.fromBytes(command.metaInfo())) {
            case MetaInfo metaInfo -> {
                switch (state.getTorrents().get(metaInfo.info().hash())) {
                    case ClientState.Torrent ignored ->
                            mailbox.reply(new ClientCommandResult.Failure(metaInfo.info().hash(), "Torrent already exists"));

                    case null -> {
                        var announcerRef = mailbox.system()
                                .spawn(new AnnouncerMailboxReceiver(
                                        context,
                                        AnnouncerState.builder()
                                                .status(StatusType.Stopped)
                                                .infoHash(metaInfo.info().hash())
                                                .selfPeerId(state.getSelfPeerId())
                                                .announce(metaInfo.announce())
                                                .peerCount(state.getSettings().getPeerCount())
                                                .port(state.getSettings().getPort())
                                                .intervalSeconds(state.getSettings().getIntervalSeconds())
                                                .downloaded(0L)
                                                .uploaded(0L)
                                                .left(metaInfo.info().length())
                                                .build(),
                                        mailbox.self()));
                        var torrentRef = mailbox.system()
                                .spawn(new TorrentMailboxReceiver(
                                        context,
                                        TorrentState.builder()
                                                .status(StatusType.Stopped)
                                                .infoHash(metaInfo.info().hash())
                                                .selfPeerId(state.getSelfPeerId())
                                                .bitfield(new Bitfield())
                                                .name(metaInfo.info().name())
                                                .announce(metaInfo.announce())
                                                .pieceLength(metaInfo.info().pieceLength())
                                                .length(metaInfo.info().length())
                                                .downloaded(0L)
                                                .uploaded(0L)
                                                .left(metaInfo.info().length())
                                                .downloadRate(0.0)
                                                .uploadRate(0.0)
                                                .build(),
                                        mailbox.self(),
                                        announcerRef));
                        state.getTorrents()
                                .add(ClientState.Torrent.builder()
                                             .ref(torrentRef)
                                             .status(StatusType.Stopped)
                                             .infoHash(metaInfo.info().hash())
                                             .selfPeerId(state.getSelfPeerId())
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
        return NextState.Receive;
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientCommand.Remove command) {
        switch (state.getTorrents().remove(command.infoHash())) {
            case ClientState.Torrent torrent -> {
                torrent.getRef().post(TorrentCommand.Terminate.INSTANCE, mailbox.self());
                mailbox.reply(new ClientCommandResult.Success(command.infoHash(), "Torrent removed"));
            }

            case null -> mailbox.reply(new ClientCommandResult.Failure(command.infoHash(), "Torrent doesn't exist"));
        }
        return NextState.Receive;
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientCommand.Start command) {
        switch (state.getTorrents().get(command.infoHash())) {
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
        return NextState.Receive;
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientCommand.Stop command) {
        switch (state.getTorrents().get(command.infoHash())) {
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
        return NextState.Receive;
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientRequest request) {
        return switch (request) {
            case ClientRequest.Get get -> handleGetRequest(mailbox, get);
        };
    }

    private NextState handleGetRequest(final Mailbox.Success mailbox, final ClientRequest.Get request) {
        switch (state.getTorrents().get(request.infoHash())) {
            case ClientState.Torrent torrent -> mailbox.reply(new ClientResponse.Get(ClientResponse.Torrent.of(torrent)));

            case null -> mailbox.reply(new ClientResponse.Failure(request.infoHash(), "Torrent doesn't exist"));
        }
        return NextState.Receive;
    }

    private NextState handle(final Mailbox.Success mailbox, final TorrentNotification notification) {
        return switch (notification) {
            case TorrentNotification.StatusChanged statusChanged -> handleStatusChangedTorrentNotification(mailbox, statusChanged);
            case TorrentNotification.Terminated terminated -> handleTerminatedTorrentNotification(mailbox, terminated);
            case TorrentNotification.Failure failure -> handleFailureTorrentNotification(mailbox, failure);
        };
    }

    private NextState handleStatusChangedTorrentNotification(
            final Mailbox.Success mailbox,
            final TorrentNotification.StatusChanged notification) {
        switch (state.getTorrents().get(notification.infoHash())) {
            case ClientState.Torrent torrent -> torrent.setStatus(notification.status());

            case null -> log.error("Torrent notification for non-existent torrent '{}'", notification.infoHash());
        }
        return NextState.Receive;
    }

    private NextState handleTerminatedTorrentNotification(
            final Mailbox.Success mailbox,
            final TorrentNotification.Terminated notification) {
        switch (state.getTorrents().remove(notification.infoHash())) {
            case ClientState.Torrent torrent -> { /* Ignored. */ }

            case null -> log.error("Torrent notification for non-existent torrent '{}'", notification.infoHash());
        }
        return NextState.Receive;
    }

    private NextState handleFailureTorrentNotification(
            final Mailbox mailbox,
            final TorrentNotification.Failure failure) {
        log.error("Torrent {} failed", failure.infoHash(), failure.cause());
        return NextState.Receive;
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification notification) {
        switch (state.getTorrents().get(notification.infoHash())) {
            case ClientState.Torrent torrent -> torrent.getRef().post(notification, mailbox.sender());

            case null -> { /* Ignored. */ }
        }
        return NextState.Receive;
    }

    private NextState handle(final Mailbox.Failure mailbox) {
        switch (mailbox.message()) {
            case ClientCommand command -> {
                log.error("Failed to handle command", mailbox.cause());
                mailbox.reply(new ClientCommandResult.Failure(
                        InfoHash.BLANK,
                        "Command failed with '%s'".formatted(mailbox.cause().getMessage())));
            }

            case ClientRequest request -> {
                log.error("Failed to handle request", mailbox.cause());
                mailbox.reply(new ClientResponse.Failure(
                        InfoHash.BLANK,
                        "Request failed with '%s'".formatted(mailbox.cause().getMessage())));
            }

            case TorrentNotification notification -> log.error("Failed to handle torrent notification", mailbox.cause());

            default -> log.error("Failed", mailbox.cause());
        }
        return NextState.Receive;
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        log.error("Unhandled message {}", mailbox.message());
        return NextState.Receive;
    }
}
