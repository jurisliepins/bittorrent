package com.github.jurisliepins;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class Awaiter<T> implements ActorReceiver {

    private final CountDownLatch latch = new CountDownLatch(1);

    private T result;

    @SuppressWarnings("unchecked")
    @Override
    public NextState receive(final Envelope envelope) {
        switch (envelope) {
            case Envelope.Success success -> {
                result = (T) success.message();
                latch.countDown();
            }
            case Envelope.Failure ignored -> {
                result = null;
                latch.countDown();
            }
        }
        return NextState.Terminate;
    }

    public T awaitResult() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new ActorException("Failed to await result of ask.", e);
        }
        return result;
    }

    public T awaitResult(final long timeout, final TimeUnit unit) {
        try {
            if (!latch.await(timeout, unit)) {
                throw new ActorException("Failed to await result of ask.");
            }
        } catch (InterruptedException e) {
            throw new ActorException("Failed to await result of ask.", e);
        }
        return result;
    }
}
