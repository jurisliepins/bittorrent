package com.github.jurisliepins;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class MailboxReceiverTests {

    protected static final int TIMEOUT_MS = 10_000;

    protected ActorSystem system;

    @BeforeEach
    void setUp() {
        system = new ActorSystem();
    }

    @AfterEach
    void tearDown() {
        system.shutdown();
    }
}
