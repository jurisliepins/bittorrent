package com.github.jurisliepins.announcer;

import com.github.jurisliepins.CoreStateContextMailboxReceiver;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.context.Context;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class AnnouncerMailboxReceiver extends CoreStateContextMailboxReceiver<AnnouncerState> {
    public AnnouncerMailboxReceiver(
            @NonNull final Context context,
            @NonNull final AnnouncerState state) {
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
            case AnnouncerCommand command -> handle(mailbox, command);
            default -> unhandled(mailbox);
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerCommand command) {
        return switch (command) {
            case AnnouncerCommand.Announce announce -> handle(mailbox, announce);
            case AnnouncerCommand.Start start -> handle(mailbox, start);
            case AnnouncerCommand.Stop stop -> handle(mailbox, stop);
            case AnnouncerCommand.Terminate terminate -> handle(mailbox, terminate);
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerCommand.Announce command) {
        return context().handlers()
                .announcer()
                .command()
                .announce()
                .handle(context(), state(), mailbox, command);
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerCommand.Start command) {
        return context().handlers()
                .announcer()
                .command()
                .start()
                .handle(context(), state(), mailbox, command);
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerCommand.Stop command) {
        return context().handlers()
                .announcer()
                .command()
                .stop()
                .handle(context(), state(), mailbox, command);
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerCommand.Terminate command) {
        return context().handlers()
                .announcer()
                .command()
                .terminate()
                .handle(context(), state(), mailbox, command);
    }

    private NextState handle(final Mailbox.Failure mailbox) {
        return context().handlers()
                .announcer()
                .failure()
                .handle(context(), state(), mailbox);
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        log.error("[{}] Unhandled message {}", state().getInfoHash(), mailbox.message());
        return NextState.Receive;
    }
}
