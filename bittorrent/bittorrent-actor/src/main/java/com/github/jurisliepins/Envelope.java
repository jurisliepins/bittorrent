package com.github.jurisliepins;

import java.util.Objects;

public sealed interface Envelope permits Envelope.Success, Envelope.Failure {
    record Success(Object message, ActorSystem system, ActorRef self, ActorRef sender) implements Envelope {
        public Success {
            Objects.requireNonNull(message, "message must not be null");
            Objects.requireNonNull(system, "system must not be null");
            Objects.requireNonNull(self, "self must not be null");
            Objects.requireNonNull(sender, "sender must not be null");
        }
    }

    record Failure(Throwable cause, ActorSystem system) implements Envelope {
        public Failure {
            Objects.requireNonNull(cause, "cause must not be null");
            Objects.requireNonNull(system, "system must not be null");
        }
    }
}
