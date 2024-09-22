package com.github.jurisliepins.handler;

import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.context.Context;

public interface CoreContextSuccessHandler<TState, TMessage> {

    NextState handle(Context context, TState state, Mailbox.Success mailbox, TMessage message);

}
