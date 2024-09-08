package com.github.jurisliepins;

import lombok.NonNull;

public abstract class CoreMailboxNotifiedStateLoggingReceiver<T, U> extends CoreMailboxStateLoggingReceiver<U> {
    private final ActorRef notifiedRef;

    public CoreMailboxNotifiedStateLoggingReceiver(@NonNull final ActorRef notifiedRef, @NonNull final U state) {
        super(state);
        this.notifiedRef = notifiedRef;
    }

    @SafeVarargs
    protected final NextState receiveNext(@NonNull final T... notifications) {
        notify(notifications);
        return receiveNext();
    }

    @SafeVarargs
    protected final NextState terminate(@NonNull final T... notifications) {
        notify(notifications);
        return terminate();
    }

    @SafeVarargs
    private void notify(@NonNull final T... notifications) {
        for (var notification : notifications) {
            notifiedRef.post(notification);
        }
    }
}
