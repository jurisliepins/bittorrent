package com.github.jurisliepins.mailbox;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.ActorSystem;
import com.github.jurisliepins.Letter;

import java.util.Objects;

public record MailboxSuccess(Object message, ActorSystem system, ActorRef self, ActorRef sender) implements Mailbox {
    public MailboxSuccess {
        Objects.requireNonNull(message, "message must not be null");
        Objects.requireNonNull(system, "system must not be null");
        Objects.requireNonNull(self, "self must not be null");
        Objects.requireNonNull(sender, "sender must not be null");
    }

    public static MailboxSuccess of(final Letter letter) {
        return new MailboxSuccess(letter.message(), letter.system(), letter.self(), letter.sender());
    }
}
