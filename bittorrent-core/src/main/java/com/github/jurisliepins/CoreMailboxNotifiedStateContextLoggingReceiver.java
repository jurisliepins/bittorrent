package com.github.jurisliepins;

import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.types.State;
import com.github.jurisliepins.types.StatusType;
import lombok.NonNull;

public abstract class CoreMailboxNotifiedStateContextLoggingReceiver<T extends State, U> extends CoreMailboxStateContextLoggingReceiver<T> {
    private final ActorRef notifiedRef;

    public CoreMailboxNotifiedStateContextLoggingReceiver(
            @NonNull final Context context,
            @NonNull final T state,
            @NonNull final ActorRef notifiedRef) {
        super(context, state);
        this.notifiedRef = notifiedRef;
    }

    @SafeVarargs
    protected final NextState receiveNext(@NonNull final U... notifications) {
        notify(notifications);
        return receiveNext();
    }

    @SafeVarargs
    protected final NextState receiveNext(final StatusType status, @NonNull final U... notifications) {
        notify(notifications);
        return receiveNext(status);
    }

    @SafeVarargs
    protected final NextState terminate(@NonNull final U... notifications) {
        notify(notifications);
        return terminate();
    }

    @SafeVarargs
    protected final NextState terminate(final StatusType status, @NonNull final U... notifications) {
        notify(notifications);
        return terminate(status);
    }

    @SafeVarargs
    private void notify(@NonNull final U... notifications) {
        for (var notification : notifications) {
            notifiedRef.post(notification);
        }
    }
}
