package com.github.jurisliepins.actors;

import com.github.jurisliepins.ActorSystem;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@Singleton
public final class ActorSystemProducer {

    @Produces
    public ActorSystem actorSystem() {
        return new ActorSystem();
    }

}
