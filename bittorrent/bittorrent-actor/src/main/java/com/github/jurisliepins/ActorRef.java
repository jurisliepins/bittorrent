package com.github.jurisliepins;

public interface ActorRef {
    ActorRef post(Object message, ActorRef sender);

    ActorRef post(Object message);
}
