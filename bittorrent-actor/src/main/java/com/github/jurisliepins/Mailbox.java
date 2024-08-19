package com.github.jurisliepins;

import java.util.Objects;

public sealed interface Mailbox permits Mailbox.Success, Mailbox.Failure {

    record Success(Object message, ActorSystem system, ActorRef self, ActorRef sender) implements Mailbox {
        public Success {
            Objects.requireNonNull(message);
            Objects.requireNonNull(system);
            Objects.requireNonNull(self);
            Objects.requireNonNull(sender);
        }
    }

    record Failure(Object message, ActorSystem system, ActorRef self, ActorRef sender, Throwable cause) implements Mailbox {
        public Failure {
            Objects.requireNonNull(message);
            Objects.requireNonNull(system);
            Objects.requireNonNull(self);
            Objects.requireNonNull(sender);
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
