package com.github.jurisliepins;

import com.github.jurisliepins.mailbox.Mailbox;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class Awaiter<T> implements ActorReceiver {

    private final CountDownLatch latch = new CountDownLatch(1);

    private T result;

    @SuppressWarnings("unchecked")
    @Override
    public NextState receive(final Mailbox mailbox) {
        switch (mailbox.status()) {
            case Success -> {
                result = (T) mailbox.message();
                latch.countDown();
            }
            case Failure -> {
                result = null;
                latch.countDown();
            }
            default -> throw new ActorException("Should not have reached this case");
        }
        return NextState.Terminate;
    }

    public T awaitResult() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new ActorException("Failed to await result", e);
        }
        return result;
    }

    public T awaitResult(final long timeout, final TimeUnit unit) {
        try {
            if (!latch.await(timeout, unit)) {
                throw new ActorException("Failed to await result");
            }
        } catch (InterruptedException e) {
            throw new ActorException("Failed to await result", e);
        }
        return result;
    }
}
