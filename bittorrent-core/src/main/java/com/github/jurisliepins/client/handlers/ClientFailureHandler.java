package com.github.jurisliepins.client.handlers;

import com.github.jurisliepins.handler.CoreContextFailureHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.context.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ClientFailureHandler implements CoreContextFailureHandler<ClientState> {

    @Override
    public NextState handle(final Context context, final Mailbox.Failure mailbox, final ClientState state) {
        log.error("Failure", mailbox.cause());
        return NextState.Receive;
    }

}
