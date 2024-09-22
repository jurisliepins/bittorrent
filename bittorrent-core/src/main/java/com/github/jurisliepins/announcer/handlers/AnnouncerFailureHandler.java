package com.github.jurisliepins.announcer.handlers;

import com.github.jurisliepins.handler.CoreContextFailureHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.AnnouncerState;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.context.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class AnnouncerFailureHandler implements CoreContextFailureHandler<AnnouncerState> {

    @Override
    public NextState handle(final Context context, final AnnouncerState state, final Mailbox.Failure mailbox) {
        switch (mailbox.message()) {
            case AnnouncerCommand ignored -> {
                log.error("[{}] Failed to handle command", state.getInfoHash(), mailbox.cause());
                state.getNotifiedRef().post(new AnnouncerNotification.Failure(state.getInfoHash(), mailbox.cause()), mailbox.self());
            }
            default -> {
                log.error("[{}] Failed", state.getInfoHash(), mailbox.cause());
                state.getNotifiedRef().post(new AnnouncerNotification.Failure(state.getInfoHash(), mailbox.cause()), mailbox.self());
            }
        }
        return NextState.Receive;
    }
}
