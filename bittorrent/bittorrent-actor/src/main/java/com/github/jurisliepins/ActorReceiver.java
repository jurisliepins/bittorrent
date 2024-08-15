package com.github.jurisliepins;

import com.github.jurisliepins.mailbox.Mailbox;

@FunctionalInterface
public interface ActorReceiver {
    NextState receive(Mailbox mailbox);
}
