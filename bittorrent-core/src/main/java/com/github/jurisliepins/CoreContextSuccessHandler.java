package com.github.jurisliepins;

import com.github.jurisliepins.context.Context;

public interface CoreContextSuccessHandler<T, U> {

    NextState handle(Context context, Mailbox.Success mailbox, T state, U message);

}
