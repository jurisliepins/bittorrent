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

        public static Envelope fromLetter(final Letter letter) {
            return new Success(letter.message(), letter.system(), letter.self(), letter.sender());
        }
    }

    record Failure(Object message, ActorSystem system, ActorRef self, ActorRef sender, Throwable cause) implements Envelope {
        public Failure {
            Objects.requireNonNull(message, "message must not be null");
            Objects.requireNonNull(system, "system must not be null");
            Objects.requireNonNull(self, "self must not be null");
            Objects.requireNonNull(sender, "sender must not be null");
            Objects.requireNonNull(cause, "cause must not be null");
        }

        public static Envelope fromLetter(final Letter letter, final Throwable cause) {
            return new Failure(letter.message(), letter.system(), letter.self(), letter.sender(), cause);
        }
    }

    default <T> void reply(final T replyMessage) {
        switch (this) {
            case Success success -> success.sender().post(replyMessage);
            case Failure failure -> failure.sender().post(replyMessage);
        }
    }

    default <T> void reply(final T replyMessage, final ActorRef replySender) {
        switch (this) {
            case Success success -> success.sender().post(replyMessage, replySender);
            case Failure failure -> failure.sender().post(replyMessage, replySender);
        }
    }
}
