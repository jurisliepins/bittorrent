package com.github.jurisliepins;

import com.github.jurisliepins.context.Context;

public abstract class CoreContextMailboxReceiver implements MailboxReceiver {

    private final Context context;

    protected CoreContextMailboxReceiver(final Context context) {
        this.context = context;
    }

    protected Context context() {
        return context;
    }
}
