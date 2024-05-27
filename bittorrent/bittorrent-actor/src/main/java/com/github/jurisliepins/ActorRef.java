package com.github.jurisliepins;

import java.util.concurrent.TimeUnit;

public interface ActorRef {
    <T> ActorRef post(T message, ActorRef sender);

    <T> ActorRef post(T message);

    <T, U> T ask(U message);

    <T, U> T ask(U message, long timeout, TimeUnit unit);
}
