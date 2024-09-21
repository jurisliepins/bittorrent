package com.github.jurisliepins;

import com.github.jurisliepins.context.Context;
import lombok.NonNull;

public abstract class CoreMailboxContextLoggingReceiver implements MailboxReceiver {
    private final Context context;

    public CoreMailboxContextLoggingReceiver(@NonNull final Context context) {
        this.context = context;
    }

    protected Context context() {
        return context;
    }
}
