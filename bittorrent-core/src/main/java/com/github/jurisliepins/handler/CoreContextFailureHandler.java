package com.github.jurisliepins.handler;

import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.context.Context;

public interface CoreContextFailureHandler<T> {

    NextState handle(Context context, Mailbox.Failure mailbox, T state);

}
