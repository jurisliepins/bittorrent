package com.github.jurisliepins.announcer.handlers.command;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.AnnouncerState;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.common.StatusType;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.tracker.TrackerEventType;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public final class AnnouncerCommandStartHandler implements CoreContextSuccessHandler<AnnouncerState, AnnouncerCommand.Start> {

    @Override
    public NextState handle(
            final Context context,
            final AnnouncerState state,
            final Mailbox.Success mailbox,
            final AnnouncerCommand.Start message) {
        switch (state.getStatus()) {
            case Stopped -> {
                mailbox.self().post(new AnnouncerCommand.Announce(Optional.of(TrackerEventType.Started)), mailbox.self());

                state.setStatus(StatusType.Started);
                state.getNotifiedRef()
                        .post(new AnnouncerNotification.StatusChanged(state.getInfoHash(), StatusType.Started), mailbox.self());

                log.info("[{}] Announcer started", state.getInfoHash());
            }
            default -> log.info("[{}] Announcer already started", state.getInfoHash());
        }
        return NextState.Receive;
    }
}
