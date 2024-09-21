package com.github.jurisliepins;

import com.github.jurisliepins.context.Context;

public interface CoreContextFailureHandler<T> {

    NextState handle(Context context, Mailbox.Failure mailbox, T state);

}
