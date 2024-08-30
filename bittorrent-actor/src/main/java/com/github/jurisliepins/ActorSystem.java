package com.github.jurisliepins;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ActorSystem {
    private static final int SHUTDOWN_TIMEOUT_MS = 1000;

    private final ExecutorService executorService = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual()
                    .name("actor-thread-", 0)
                    .factory());

    public ActorRef spawn(final MailboxReceiver receiver) {
        final Actor.BlockingQueue actor = new Actor.BlockingQueue(this, receiver);
        executorService.execute(actor);
        return actor;
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
