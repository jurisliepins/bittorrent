package com.github.jurisliepins;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ActorSystem {
    private static final int SHUTDOWN_TIMEOUT_MS = 1000;

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public ActorRef spawn(final ActorReceiver receiver) {
        final Actor.RunnableActor actor = new Actor.RunnableActor(this, receiver);
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
