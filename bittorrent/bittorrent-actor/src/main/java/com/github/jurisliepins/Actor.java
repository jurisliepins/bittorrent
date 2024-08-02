package com.github.jurisliepins;

import java.util.Objects;
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
        public <T, U> U postWithReply(final T message) {
            throw new ActorException("Cannot post to a blank actor");
        }

        @Override
        public <T, U> U postWithReply(final T message, final long timeout, final TimeUnit unit) {
            throw new ActorException("Cannot post to a blank actor");
        }
    }

    class RunnableActor implements ActorRef, Runnable {
        private final LinkedBlockingQueue<Envelope> mailbox = new LinkedBlockingQueue<>();
        private final ActorSystem system;
        private final ActorReceiver receiver;

        public RunnableActor(final ActorSystem system, final ActorReceiver receiver) {
            this.system = Objects.requireNonNull(system, "system is null");
            this.receiver = Objects.requireNonNull(receiver, "receiver is null");
        }

        public <T> ActorRef post(final T message, final ActorRef sender) {
            Objects.requireNonNull(message, "message is null");
            mailbox.add(new Envelope.Success(message, system, this, sender));
            return this;
        }

        public <T> ActorRef post(final T message) {
            Objects.requireNonNull(message, "message is null");
            mailbox.add(new Envelope.Success(message, system, this, BlankActor.INSTANCE));
            return this;
        }

        @Override
        public <T, U> U postWithReply(final T message) {
            Objects.requireNonNull(message, "message is null");
            final Awaiter<U> awaiter = new Awaiter<>();
            final ActorRef awaiterRef = system.spawn(awaiter);
            post(message, awaiterRef);
            return awaiter.awaitResult();
        }

        @Override
        public <T, U> U postWithReply(final T message, final long timeout, final TimeUnit unit) {
            Objects.requireNonNull(message, "message is null");
            final Awaiter<U> awaiter = new Awaiter<>();
            final ActorRef awaiterRef = system.spawn(awaiter);
            post(message, awaiterRef);
            return awaiter.awaitResult(timeout, unit);
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
                    nextState = receiver.receive(mailbox.take());
                } catch (Throwable cause) {
                    try {
                        final NextState ignored = receiver.receive(new Envelope.Failure(cause, system));
                    } catch (Throwable ignored) {
                        // Ignored.
                    }
                    return;
                }
            } while (nextState == NextState.Receive);
        }
    }
}
