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

public final class AnnouncerCommandStopHandler implements CoreContextSuccessHandler<AnnouncerState, AnnouncerCommand.Stop> {

    @Override
    public NextState handle(
            final Context context,
            final Mailbox.Success mailbox,
            final AnnouncerState state,
            final AnnouncerCommand.Stop message) {
        switch (state.getStatus()) {
            case Started, Errored -> {
                mailbox.self()
                        .post(new AnnouncerCommand.Announce(Optional.of(TrackerEventType.Stopped)), mailbox.self());

                state.setStatus(StatusType.Stopped);
                state.getNotifiedRef()
                        .post(new AnnouncerNotification.StatusChanged(state.getInfoHash(), StatusType.Stopped), mailbox.self());

                context.log()
                        .announcer()
                        .info("[{}] Announcer stopped", state.getInfoHash());
            }
            default -> {
                context.log()
                        .announcer()
                        .info("[{}] Announcer already stopped", state.getInfoHash());
            }
        }
        return NextState.Receive;
    }

}
