package com.github.jurisliepins;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public interface Actor {
    enum NextState {
        Receive,
        Terminate
    }

    @FunctionalInterface
    interface Receiver {
        NextState receive(Envelope envelope);
    }

    sealed interface Envelope permits Envelope.Success, Envelope.Failure {
        record Success(Object message, System system, ActorRef self, ActorRef sender) implements Envelope {
            public Success {
                Objects.requireNonNull(message, "message must not be null");
                Objects.requireNonNull(system, "system must not be null");
                Objects.requireNonNull(self, "self must not be null");
                Objects.requireNonNull(sender, "sender must not be null");
            }
        }

        record Failure(Throwable cause, System system) implements Envelope {
            public Failure {
                Objects.requireNonNull(cause, "cause must not be null");
                Objects.requireNonNull(system, "system must not be null");
            }
        }
    }

    interface ActorRef {
        ActorRef post(Object message, ActorRef sender);

        ActorRef post(Object message);
    }

    class BlankActor implements ActorRef {
        public static final BlankActor INSTANCE = new BlankActor();

        @Override
        public ActorRef post(final Object message, final ActorRef sender) {
            return this;
        }

        @Override
        public ActorRef post(final Object message) {
            return this;
        }
    }

    class RunnableActor implements ActorRef, Runnable {
        private final LinkedBlockingQueue<Envelope> mailbox = new LinkedBlockingQueue<>();
        private final System actorSystem;
        private final Receiver actorReceiver;

        public RunnableActor(final System system, final Receiver receiver) {
            this.actorSystem = system;
            this.actorReceiver = receiver;
        }

        public ActorRef post(final Object message, final ActorRef sender) {
            mailbox.add(new Envelope.Success(message, actorSystem, this, sender));
            return this;
        }

        public ActorRef post(final Object message) {
            mailbox.add(new Envelope.Success(message, actorSystem, this, BlankActor.INSTANCE));
            return this;
        }

        public void run() {
            while (true) {
                try {
                    // This actor relies on the fact that blocking operations ran inside virtual threads will now be
                    // unmounted from platform thread when blocking is detected with the stack copied into heap memory.
                    // Once the operation unblocks, the stack is mounted back to the platform thread and execution is
                    // resumed. All blocking operations have been refactored in Java21,
                    // including the LinkedBlockingQueue.
                    //
                    // Explanation https://www.youtube.com/watch?v=5E0LU85EnTI.
                    //
                    // Actor setup inspired by
                    // https://www.javaadvent.com/2022/12/actors-and-virtual-threads-a-match-made-in-heaven.html.
                    final Envelope envelope = mailbox.take();
                    switch (actorReceiver.receive(envelope)) {
                        case Receive:
                            continue;
                        case Terminate:
                            return;
                        default:
                            return;
                    }
                } catch (Throwable cause) {
                    try {
                        final NextState ignored = actorReceiver.receive(new Envelope.Failure(cause, actorSystem));
                    } catch (Throwable ignored) {
                        // Ignored.
                    }
                    return;
                }
            }
        }
    }

    class System {
        private static final int SHUTDOWN_TIMEOUT_MS = 1000;

        private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

        public ActorRef spawn(final Receiver receiver) {
            final RunnableActor actor = new RunnableActor(this, receiver);
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
}
