package com.github.jurisliepins.handler;

import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.context.Context;

public interface CoreContextFailureHandler<TState> {

    NextState handle(Context context, TState state, Mailbox.Failure mailbox);

}
