package com.github.jurisliepins.actors;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.ActorSystem;
import com.github.jurisliepins.client.Client;
import com.github.jurisliepins.client.ClientState;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@Singleton
public final class ClientProducer {

    @Produces
    public ActorRef clientRef(final ActorSystem actorSystem) {
        return actorSystem.spawn(new Client(new ClientState()));
    }

}
