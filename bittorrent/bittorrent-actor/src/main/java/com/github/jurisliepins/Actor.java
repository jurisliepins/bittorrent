package com.github.jurisliepins;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public interface Actor {
    class BlankActor implements ActorRef {
        public static final BlankActor INSTANCE = new BlankActor();

        @Override
        public <T> ActorRef post(final T message, final ActorRef sender) {
            return this;
        }

        @Override
        public <T> ActorRef post(final T message) {
            return this;
        }

        @Override
        public <T, U> T ask(final U message) {
            throw new ActorException("Cannot ask a blank actor.");
        }

        @Override
        public <T, U> T ask(final U message, final long timeout, final TimeUnit unit) {
            throw new ActorException("Cannot ask a blank actor.");
        }
    }

    class RunnableActor implements ActorRef, Runnable {
        private final LinkedBlockingQueue<Envelope> mailbox = new LinkedBlockingQueue<>();
        private final ActorSystem actorSystem;
        private final ActorReceiver actorReceiver;

        public RunnableActor(final ActorSystem system, final ActorReceiver receiver) {
            actorSystem = system;
            actorReceiver = receiver;
        }

        public <T> ActorRef post(final T message, final ActorRef sender) {
            mailbox.add(new Envelope.Success(message, actorSystem, this, sender));
            return this;
        }

        public <T> ActorRef post(final T message) {
            mailbox.add(new Envelope.Success(message, actorSystem, this, BlankActor.INSTANCE));
            return this;
        }

        @Override
        public <T, U> T ask(final U message) {
            final Awaiter<T> awaiter = new Awaiter<>();
            final ActorRef awaiterRef = actorSystem.spawn(awaiter);
            post(message, awaiterRef);
            return awaiter.result();
        }

        @Override
        public <T, U> T ask(final U message, final long timeout, final TimeUnit unit) {
            final Awaiter<T> awaiter = new Awaiter<>();
            final ActorRef awaiterRef = actorSystem.spawn(awaiter);
            post(message, awaiterRef);
            return awaiter.result(timeout, unit);
        }

        public void run() {
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
            //
            // We continue iterating until we get NextState.Terminate.
            NextState nextState;
            do {
                try {
                    nextState = actorReceiver.receive(mailbox.take());
                } catch (Throwable cause) {
                    try {
                        final NextState ignored = actorReceiver.receive(new Envelope.Failure(cause, actorSystem));
                    } catch (Throwable ignored) {
                        // Ignored.
                    }
                    return;
                }
            } while (nextState == NextState.Receive);
        }
    }
}
