package com.github.jurisliepins;

import com.github.jurisliepins.context.Context;

public abstract class CoreStateContextMailboxReceiver<TState> extends CoreContextMailboxReceiver {

    private final TState state;

    protected CoreStateContextMailboxReceiver(
            final Context context,
            final TState state) {
        super(context);
        this.state = state;
    }

    protected TState state() {
        return state;
    }
}
