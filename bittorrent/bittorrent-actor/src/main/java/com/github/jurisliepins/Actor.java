package com.github.jurisliepins;

import java.util.concurrent.LinkedBlockingQueue;

public interface Actor {
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
        private final LinkedBlockingQueue<Envelope> actorMailbox = new LinkedBlockingQueue<>();
        private final ActorSystem actorSystem;
        private final Receiver actorReceiver;

        public RunnableActor(final ActorSystem system, final Receiver receiver) {
            this.actorSystem = system;
            this.actorReceiver = receiver;
        }

        public ActorRef post(final Object message, final ActorRef sender) {
            actorMailbox.add(new Envelope.Success(message, actorSystem, this, sender));
            return this;
        }

        public ActorRef post(final Object message) {
            actorMailbox.add(new Envelope.Success(message, actorSystem, this, BlankActor.INSTANCE));
            return this;
        }

        public void run() {
            NextState nextState = NextState.Receive;
            while (nextState == NextState.Receive) {
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
                    //
                    // We continue iterating until we get NextState.Terminate.
                    nextState = actorReceiver.receive(actorMailbox.take());
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
}
