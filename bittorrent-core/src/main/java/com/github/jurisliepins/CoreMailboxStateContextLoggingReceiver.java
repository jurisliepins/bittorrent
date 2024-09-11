package com.github.jurisliepins;

import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.types.State;
import com.github.jurisliepins.types.StatusType;
import lombok.NonNull;

public abstract class CoreMailboxStateContextLoggingReceiver<T extends State> extends CoreMailboxContextLoggingReceiver {
    private final T state;

    public CoreMailboxStateContextLoggingReceiver(
            @NonNull final Context context,
            @NonNull final T state) {
        super(context);
        this.state = state;
    }

    protected final T state() {
        return state;
    }

    protected final NextState receiveNext() {
        return NextState.Receive;
    }

    protected final NextState receiveNext(final StatusType status) {
        state.setStatus(status);
        return NextState.Receive;
    }

    protected final NextState terminate() {
        return NextState.Terminate;
    }

    protected final NextState terminate(final StatusType status) {
        state.setStatus(status);
        return NextState.Terminate;
    }
}
