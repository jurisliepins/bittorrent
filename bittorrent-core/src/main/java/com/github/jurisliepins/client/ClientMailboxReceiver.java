package com.github.jurisliepins.client;

import com.github.jurisliepins.CoreStateContextMailboxReceiver;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.client.message.ClientCommand;
import com.github.jurisliepins.client.message.ClientRequest;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ClientMailboxReceiver extends CoreStateContextMailboxReceiver<ClientState> {
    public ClientMailboxReceiver(
            @NonNull final Context context,
            @NonNull final ClientState state) {
        super(context, state);
    }

    @Override
    public NextState receive(final Mailbox mailbox) {
        return switch (mailbox) {
            case Mailbox.Success success -> handle(success);
            case Mailbox.Failure failure -> handle(failure);
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
        return context().handlers()
                .client()
                .command()
                .add()
                .handle(context(), state(), mailbox, command);
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientCommand.Remove command) {
        return context().handlers()
                .client()
                .command()
                .remove()
                .handle(context(), state(), mailbox, command);
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientCommand.Start command) {
        return context().handlers()
                .client()
                .command()
                .start()
                .handle(context(), state(), mailbox, command);
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientCommand.Stop command) {
        return context().handlers()
                .client()
                .command()
                .stop()
                .handle(context(), state(), mailbox, command);
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientRequest request) {
        return switch (request) {
            case ClientRequest.Get get -> handle(mailbox, get);
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientRequest.Get request) {
        return context().handlers()
                .client()
                .request()
                .get()
                .handle(context(), state(), mailbox, request);
    }

    private NextState handle(final Mailbox.Success mailbox, final TorrentNotification notification) {
        return switch (notification) {
            case TorrentNotification.StatusChanged statusChanged -> handle(mailbox, statusChanged);
            case TorrentNotification.Terminated terminated -> handle(mailbox, terminated);
            case TorrentNotification.Failure failure -> handle(mailbox, failure);
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final TorrentNotification.StatusChanged statusChanged) {
        return context().handlers()
                .client()
                .notification()
                .torrent()
                .statusChanged()
                .handle(context(), state(), mailbox, statusChanged);
    }

    private NextState handle(final Mailbox.Success mailbox, final TorrentNotification.Terminated terminated) {
        return context().handlers()
                .client()
                .notification()
                .torrent()
                .terminated()
                .handle(context(), state(), mailbox, terminated);
    }

    private NextState handle(final Mailbox.Success mailbox, final TorrentNotification.Failure failure) {
        return context().handlers()
                .client()
                .notification()
                .torrent()
                .failure()
                .handle(context(), state(), mailbox, failure);
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification notification) {
        return switch (notification) {
            case AnnouncerNotification.PeersReceived peersReceived -> handle(mailbox, peersReceived);
            case AnnouncerNotification.StatusChanged statusChanged -> handle(mailbox, statusChanged);
            case AnnouncerNotification.Terminated terminated -> handle(mailbox, terminated);
            case AnnouncerNotification.Failure failure -> handle(mailbox, failure);
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification.PeersReceived peersReceived) {
        return context().handlers()
                .client()
                .notification()
                .announcer()
                .peersReceived()
                .handle(context(), state(), mailbox, peersReceived);
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification.StatusChanged statusChanged) {
        return context().handlers()
                .client()
                .notification()
                .announcer()
                .statusChanged()
                .handle(context(), state(), mailbox, statusChanged);
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification.Terminated terminated) {
        return context().handlers()
                .client()
                .notification()
                .announcer()
                .terminated()
                .handle(context(), state(), mailbox, terminated);
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification.Failure failure) {
        return context().handlers()
                .client()
                .notification()
                .announcer()
                .failure()
                .handle(context(), state(), mailbox, failure);
    }

    private NextState handle(final Mailbox.Failure mailbox) {
        return context().handlers()
                .client()
                .failure()
                .handle(context(), state(), mailbox);
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        log.error("Unhandled message {}", mailbox.message());
        return NextState.Receive;
    }
}
