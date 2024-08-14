package com.github.jurisliepins;

import java.util.Objects;

public record Letter(Object message, ActorSystem system, ActorRef self, ActorRef sender) {
    public Letter {
        Objects.requireNonNull(message, "message must not be null");
        Objects.requireNonNull(system, "system must not be null");
        Objects.requireNonNull(self, "self must not be null");
        Objects.requireNonNull(sender, "sender must not be null");
    }
}
