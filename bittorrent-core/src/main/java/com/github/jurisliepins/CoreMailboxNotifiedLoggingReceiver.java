package com.github.jurisliepins;

import lombok.NonNull;

public abstract class CoreMailboxNotifiedLoggingReceiver<T> extends CoreMailboxLoggingReceiver {
    private final ActorRef notifiedRef;

    public CoreMailboxNotifiedLoggingReceiver(@NonNull final ActorRef notifiedRef) {
        this.notifiedRef = notifiedRef;
    }

    protected final NextState receiveNext() {
        return NextState.Receive;
    }

    @SafeVarargs
    protected final NextState receiveNext(@NonNull final T... notifications) {
        notify(notifications);
        return receiveNext();
    }

    protected final NextState terminate() {
        return NextState.Terminate;
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
