package com.github.jurisliepins.announcer.handlers.command;

import com.github.jurisliepins.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.AnnouncerState;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.common.StatusType;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.tracker.TrackerEventType;

import java.util.Optional;

public final class AnnouncerCommandStartHandler implements CoreContextSuccessHandler<AnnouncerState, AnnouncerCommand.Start> {

    @Override
    public NextState handle(
            final Context context,
            final Mailbox.Success mailbox,
            final AnnouncerState state,
            final AnnouncerCommand.Start message) {
        switch (state.getStatus()) {
            case Stopped -> {
                mailbox.self()
                        .post(new AnnouncerCommand.Announce(Optional.of(TrackerEventType.Started)), mailbox.self());

                state.setStatus(StatusType.Started);
                state.getNotifiedRef()
                        .post(new AnnouncerNotification.StatusChanged(state.getInfoHash(), StatusType.Started), mailbox.self());

                context.log()
                        .announcer()
                        .info("[{}] Announcer started", state.getInfoHash());
            }
            default -> {
                context.log()
                        .announcer()
                        .info("[{}] Announcer already started", state.getInfoHash());
            }
        }
        return NextState.Receive;
    }

}
