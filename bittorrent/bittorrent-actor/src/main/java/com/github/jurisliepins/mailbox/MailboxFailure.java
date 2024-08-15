package com.github.jurisliepins.mailbox;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.ActorSystem;
import com.github.jurisliepins.Letter;

import java.util.Objects;

public record MailboxFailure(Object message, ActorSystem system, ActorRef self, ActorRef sender, Throwable cause) implements Mailbox {
    public MailboxFailure {
        Objects.requireNonNull(message, "message must not be null");
        Objects.requireNonNull(system, "system must not be null");
        Objects.requireNonNull(self, "self must not be null");
        Objects.requireNonNull(sender, "sender must not be null");
        Objects.requireNonNull(cause, "cause must not be null");
    }

    public static MailboxFailure of(final Letter letter, final Throwable cause) {
        return new MailboxFailure(letter.message(), letter.system(), letter.self(), letter.sender(), cause);
    }
}

