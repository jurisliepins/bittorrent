package com.github.jurisliepins.announcer.handlers.command;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.AnnouncerState;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.context.Context;

public final class AnnouncerCommandTerminateHandler implements CoreContextSuccessHandler<AnnouncerState, AnnouncerCommand.Terminate> {

    @Override
    public NextState handle(
            final Context context,
            final Mailbox.Success mailbox,
            final AnnouncerState state,
            final AnnouncerCommand.Terminate message) {
        state.getNotifiedRef().post(new AnnouncerNotification.Terminated(state.getInfoHash()), mailbox.self());
        return NextState.Terminate;
    }

}
