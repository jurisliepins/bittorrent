package com.github.jurisliepins;

@FunctionalInterface
public interface Receiver {
    NextState receive(Envelope envelope);
}
