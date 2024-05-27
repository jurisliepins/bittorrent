package com.github.jurisliepins;

@FunctionalInterface
public interface ActorReceiver {
    NextState receive(Envelope envelope);
}
