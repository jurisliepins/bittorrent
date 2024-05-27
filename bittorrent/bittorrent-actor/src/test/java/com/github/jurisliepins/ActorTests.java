package com.github.jurisliepins;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("Actor tests")
public final class ActorTests {

    private static final int TIMEOUT_MS = 2000;

    private ActorSystem system;

    @BeforeEach
    public void setUp() {
        system = new ActorSystem();
    }

    @AfterEach
    public void tearDown() {
        system.shutdown();
    }

    @Test
    @DisplayName("Should spawn actor")
    public void shouldSpawnActor() {
        final ActorRef ref = system.spawn(envelope -> NextState.Terminate);
        assertNotNull(ref, "Spawned actor should not be null.");
    }

    @Test
    @DisplayName("Should post message")
    public void shouldPostMessage() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        final ActorRef ref = system.spawn(envelope -> {
            switch (envelope) {
                case Envelope.Success ignored -> latch.countDown();
                case Envelope.Failure ignored -> latch.countDown();
            }
            return NextState.Receive;
        });
        ref.post("Hello, World!");

        final boolean r = latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertTrue(r, "Should not have timed out before receiving a message.");
    }

    @Test
    @DisplayName("Should ask message")
    public void shouldAskMessage() {
        final String response = "Response!";

        final ActorRef ref = system.spawn(envelope -> {
            switch (envelope) {
                case Envelope.Success success -> success.sender().post(response);
                case Envelope.Failure ignored -> { }
            }
            return NextState.Receive;
        });

        final String answer = ref.ask("Hello, World!");

        assertEquals(response, answer, "Should have received %s as response.".formatted(response));
    }

    @Test
    @DisplayName("Should ask message with timeout")
    public void shouldAskMessageWithTimeout() {
        final String response = "Response!";

        final ActorRef ref = system.spawn(envelope -> {
            switch (envelope) {
                case Envelope.Success success -> success.sender().post(response);
                case Envelope.Failure ignored -> { }
            }
            return NextState.Receive;
        });

        final String answer = ref.ask("Hello, World!", TIMEOUT_MS, TimeUnit.MILLISECONDS);

        assertEquals(response, answer, "Should have received %s as response.".formatted(response));
    }

    @Test
    @DisplayName("Should ping-pong messages")
    public void shouldPingPongMessages() throws InterruptedException {
        final int messageCount = 10;

        final CountDownLatch latch1 = new CountDownLatch(messageCount);
        final CountDownLatch latch2 = new CountDownLatch(messageCount);

        final ActorRef ref1 = system.spawn(envelope -> {
            switch (envelope) {
                case Envelope.Success success -> {
                    success.sender()
                            .post(success.message(), success.self());
                    latch1.countDown();
                }
                case Envelope.Failure ignored -> { }
            }
            return NextState.Receive;
        });
        final ActorRef ref2 = system.spawn(envelope -> {
            switch (envelope) {
                case Envelope.Success success -> {
                    success.sender()
                            .post(success.message(), success.self());
                    latch2.countDown();
                }
                case Envelope.Failure ignored -> { }
            }
            return NextState.Receive;
        });
        ref1.post("Hello, World!", ref2);

        final boolean r1 = latch1.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        final boolean r2 = latch2.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertTrue(r1, "Actor 1 should have received %d messages.".formatted(messageCount));
        assertTrue(r2, "Actor 2 should have received %d messages.".formatted(messageCount));
    }

    @Test
    @DisplayName("Should transition states")
    public void shouldTransitionStates() throws InterruptedException {
        final int messageCount = 10;

        final CountDownLatch latch = new CountDownLatch(messageCount);

        final ActorRef ref = system.spawn(envelope -> switch (envelope) {
            case Envelope.Success success -> switch (success.message()) {
                case String command -> {
                    if ("receive".equals(command)) {
                        latch.countDown();
                        yield NextState.Receive;
                    }
                    if ("terminate".equals(command)) {
                        yield NextState.Terminate;
                    }
                    throw new RuntimeException();
                }
                default -> throw new RuntimeException();
            };
            case Envelope.Failure ignored -> throw new RuntimeException();
        });

        ref.post("receive");
        ref.post("receive");
        ref.post("receive");
        ref.post("receive");
        ref.post("receive");
        ref.post("terminate");
        ref.post("receive");
        ref.post("receive");
        ref.post("receive");
        ref.post("receive");
        ref.post("receive");

        final boolean r = latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertFalse(r, "Actor should have stopped processing messages.");

        final int c = (int) latch.getCount();
        assertEquals(messageCount / 2, c,
                "Actor should have processed only %d messages.".formatted(messageCount / 2));
    }
}
