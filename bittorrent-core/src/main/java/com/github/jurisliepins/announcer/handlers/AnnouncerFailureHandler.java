package com.github.jurisliepins.announcer.handlers;

import com.github.jurisliepins.CoreContextFailureHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.AnnouncerState;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.context.Context;

public final class AnnouncerFailureHandler implements CoreContextFailureHandler<AnnouncerState> {

    @Override
    public NextState handle(final Context context, final Mailbox.Failure mailbox, final AnnouncerState state) {
        return switch (mailbox.message()) {
            case AnnouncerCommand ignored -> {
                context.log()
                        .announcer()
                        .error("[{}] Failed to handle command", state.getInfoHash(), mailbox.cause());
                state.getNotifiedRef()
                        .post(new AnnouncerNotification.Failure(state.getInfoHash(), mailbox.cause()), mailbox.self());
                yield NextState.Receive;
            }
            default -> {
                context.log()
                        .announcer()
                        .error("[{}] Failed", state.getInfoHash(), mailbox.cause());
                state.getNotifiedRef()
                        .post(new AnnouncerNotification.Failure(state.getInfoHash(), mailbox.cause()), mailbox.self());
                yield NextState.Receive;
            }
        };
    }

}
