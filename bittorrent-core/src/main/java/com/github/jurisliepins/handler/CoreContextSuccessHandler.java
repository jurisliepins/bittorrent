package com.github.jurisliepins.handler;

import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.context.Context;

public interface CoreContextSuccessHandler<T, U> {

    NextState handle(Context context, Mailbox.Success mailbox, T state, U message);

}
