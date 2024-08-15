package com.github.jurisliepins.mailbox;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.ActorSystem;
import com.github.jurisliepins.Letter;

import java.util.Objects;

public record Mailbox(MailboxStatus status, Object message, ActorSystem system, ActorRef self, ActorRef sender, Throwable cause) {

    public Mailbox {
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(message, "message must not be null");
        Objects.requireNonNull(system, "system must not be null");
        Objects.requireNonNull(self, "self must not be null");
        Objects.requireNonNull(sender, "sender must not be null");
    }

    public <T> void reply(final T replyMessage) {
        sender().post(replyMessage);
    }

    public <T> void reply(final T replyMessage, final ActorRef replySender) {
        sender().post(replyMessage, replySender);
    }

    public static Mailbox success(final Letter letter) {
        return new Mailbox(MailboxStatus.Success, letter.message(), letter.system(), letter.self(), letter.sender(), null);
    }

    public static Mailbox failure(final Letter letter, final Throwable cause) {
        return new Mailbox(MailboxStatus.Failure, letter.message(), letter.system(), letter.self(), letter.sender(), cause);
    }
}
