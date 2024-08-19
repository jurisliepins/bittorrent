package com.github.jurisliepins;

@FunctionalInterface
public interface ActorReceiver {
    NextState receive(Mailbox mailbox);
}
