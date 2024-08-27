package com.github.jurisliepins;

@FunctionalInterface
public interface MailboxReceiver {
    NextState receive(Mailbox mailbox);
}
