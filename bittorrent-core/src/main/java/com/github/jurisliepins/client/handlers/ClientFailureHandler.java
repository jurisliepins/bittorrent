package com.github.jurisliepins.client.handlers;

import com.github.jurisliepins.CoreContextFailureHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.context.Context;

public final class ClientFailureHandler implements CoreContextFailureHandler<ClientState> {

    @Override
    public NextState handle(final Context context, final Mailbox.Failure mailbox, final ClientState state) {
        context.log()
                .client()
                .error("Failure", mailbox.cause());
        return NextState.Receive;
    }

}
