package com.github.jurisliepins.mailbox;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.ActorSystem;

public interface Mailbox {
    MailboxStatus status();

    Object message();

    ActorSystem system();

    ActorRef self();

    ActorRef sender();

    Throwable cause();

    default <T> void reply(final T replyMessage) {
        sender().post(replyMessage);
    }

    default <T> void reply(final T replyMessage, final ActorRef replySender) {
        sender().post(replyMessage, replySender);
    }
}
