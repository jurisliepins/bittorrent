package com.github.jurisliepins.client;

import com.github.jurisliepins.CoreContextMailboxReceiver;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.client.message.ClientCommand;
import com.github.jurisliepins.client.message.ClientRequest;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import lombok.NonNull;

public final class ClientMailboxReceiver extends CoreContextMailboxReceiver {
    private final ClientState state;

    public ClientMailboxReceiver(
            @NonNull final Context context,
            @NonNull final ClientState state) {
        super(context);
        this.state = state;
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
        context().log()
                .client()
                .info("Handling command {}", command);
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
                .handle(context(), mailbox, state, command);
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientCommand.Remove command) {
        return context().handlers()
                .client()
                .command()
                .remove()
                .handle(context(), mailbox, state, command);
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientCommand.Start command) {
        return context().handlers()
                .client()
                .command()
                .start()
                .handle(context(), mailbox, state, command);
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientCommand.Stop command) {
        return context().handlers()
                .client()
                .command()
                .stop()
                .handle(context(), mailbox, state, command);
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientRequest request) {
        context().log()
                .client()
                .info("Handling request {}", request);
        return switch (request) {
            case ClientRequest.Get get -> handle(mailbox, get);
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final ClientRequest.Get request) {
        return context().handlers()
                .client()
                .request()
                .get()
                .handle(context(), mailbox, state, request);
    }

    private NextState handle(final Mailbox.Success mailbox, final TorrentNotification notification) {
        context().log()
                .client()
                .info("Handling torrent notification {}", notification);
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
                .handle(context(), mailbox, state, statusChanged);
    }

    private NextState handle(final Mailbox.Success mailbox, final TorrentNotification.Terminated terminated) {
        return context().handlers()
                .client()
                .notification()
                .torrent()
                .terminated()
                .handle(context(), mailbox, state, terminated);
    }

    private NextState handle(final Mailbox.Success mailbox, final TorrentNotification.Failure failure) {
        return context().handlers()
                .client()
                .notification()
                .torrent()
                .failure()
                .handle(context(), mailbox, state, failure);
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification notification) {
        context().log()
                .client()
                .info("Handling announcer notification {}", notification);
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
                .handle(context(), mailbox, state, peersReceived);
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification.StatusChanged statusChanged) {
        return context().handlers()
                .client()
                .notification()
                .announcer()
                .statusChanged()
                .handle(context(), mailbox, state, statusChanged);
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification.Terminated terminated) {
        return context().handlers()
                .client()
                .notification()
                .announcer()
                .terminated()
                .handle(context(), mailbox, state, terminated);
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerNotification.Failure failure) {
        return context().handlers()
                .client()
                .notification()
                .announcer()
                .failure()
                .handle(context(), mailbox, state, failure);
    }

    private NextState handle(final Mailbox.Failure mailbox) {
        return context().handlers()
                .client()
                .failure()
                .handle(context(), mailbox, state);
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        context().log()
                .client()
                .error("Unhandled message {}", mailbox.message());
        return NextState.Receive;
    }
}
