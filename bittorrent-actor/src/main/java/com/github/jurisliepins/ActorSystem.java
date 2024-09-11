package com.github.jurisliepins;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ActorSystem {
    private static final int SHUTDOWN_TIMEOUT_MS = 1000;

    private final ExecutorService executorService = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual()
                    .name("actor-thread-", 0)
                    .factory());

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
            Thread.ofVirtual()
                    .name("actor-scheduled-thread-")
                    .factory());

    public ActorRef spawn(final MailboxReceiver receiver) {
        final Actor.BlockingQueue actor = new Actor.BlockingQueue(this, receiver);
        executorService.execute(actor);
        return actor;
    }

    public void schedulePostOnce(final long delay, final TimeUnit unit, final ActorRef receiverRef, final Object message) {
        scheduledExecutorService.schedule(() -> receiverRef.post(message), delay, unit);
    }

    public void schedulePostRepeating(
            final long initialDelay,
            final long period,
            final TimeUnit unit,
            final ActorRef receiverRef,
            final Object message) {
        scheduledExecutorService.scheduleAtFixedRate(() -> receiverRef.post(message), initialDelay, period, unit);
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
