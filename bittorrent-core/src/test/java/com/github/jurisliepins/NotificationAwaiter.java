package com.github.jurisliepins;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class NotificationAwaiter<T> implements MailboxReceiver {

    private final List<T> notifications;
    private final CountDownLatch latch;

    public NotificationAwaiter(final int count) {
        notifications = new ArrayList<>(count);
        latch = new CountDownLatch(count);
    }

    @SuppressWarnings("unchecked")
    @Override
    public NextState receive(final Mailbox mailbox) {
        return switch (mailbox) {
            case Mailbox.Success success -> {
                notifications.add((T) success.message());
                latch.countDown();
                yield NextState.Receive;
            }
            case Mailbox.Failure ignored -> NextState.Terminate;
        };
    }

    public List<T> awaitResult() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new ActorException("Failed to await result", e);
        }
        return notifications;
    }

    public List<T> awaitResult(final long timeout, final TimeUnit unit) {
        try {
            if (!latch.await(timeout, unit)) {
                throw new CoreException("Failed to await notifications");
            }
        } catch (InterruptedException e) {
            throw new CoreException("Failed to await notifications", e);
        }
        return notifications;
    }
}
