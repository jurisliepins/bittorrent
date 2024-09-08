package com.github.jurisliepins;

import lombok.NonNull;

public abstract class CoreMailboxStateLoggingReceiver<T> extends CoreMailboxLoggingReceiver {
    private final T state;

    public CoreMailboxStateLoggingReceiver(@NonNull final T state) {
        this.state = state;
    }

    protected final T state() {
        return state;
    }

    protected final NextState receiveNext() {
        return NextState.Receive;
    }

    protected final NextState terminate() {
        return NextState.Terminate;
    }
}
