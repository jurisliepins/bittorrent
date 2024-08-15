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
        final ActorRef ref = system.spawn(mailbox -> NextState.Terminate);
        assertNotNull(ref, "Spawned actor should not be null");
    }

    @Test
    @DisplayName("Should post message")
    public void shouldPostMessage() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        final ActorRef ref = system.spawn(mailbox -> {
            latch.countDown();
            return NextState.Receive;
        });
        ref.post("Hello, World!");

        final boolean result = latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertTrue(result, "Should not have timed out before receiving a message");
    }

    @Test
    @DisplayName("Should post message with reply")
    public void shouldPostMessageWithReply() {
        final String response = "Response!";

        final ActorRef ref = system.spawn(mailbox -> {
            switch (mailbox.status()) {
                case Success -> {
                    mailbox.sender().post(response);
                    return NextState.Receive;
                }
                case Failure -> {
                    return NextState.Receive;
                }
                default -> {
                    return NextState.Terminate;
                }
            }
        });

        final String result = ref.postWithReply("Hello, World!");
        assertEquals(response, result, "Should have received %s as response".formatted(response));
    }

    @Test
    @DisplayName("Should post message with reply with timeout")
    public void shouldPostMessageWithReplyWithTimeout() {
        final String response = "Response!";

        final ActorRef ref = system.spawn(mailbox -> {
            switch (mailbox.status()) {
                case Success -> {
                    mailbox.sender().post(response);
                    return NextState.Receive;
                }
                case Failure -> {
                    return NextState.Receive;
                }
                default -> {
                    return NextState.Terminate;
                }
            }
        });

        final String result = ref.postWithReply("Hello, World!", TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(response, result, "Should have received %s as response".formatted(response));
    }

    @Test
    @DisplayName("Should ping-pong messages")
    public void shouldPingPongMessages() throws InterruptedException {
        final int messageCount = 10;

        final CountDownLatch latch1 = new CountDownLatch(messageCount);
        final CountDownLatch latch2 = new CountDownLatch(messageCount);

        final ActorRef ref1 = system.spawn(mailbox -> {
            switch (mailbox.status()) {
                case Success -> {
                    mailbox.sender().post(mailbox.message(), mailbox.self());
                    latch1.countDown();
                    return NextState.Receive;
                }
                case Failure -> {
                    return NextState.Receive;
                }
                default -> {
                    return NextState.Terminate;
                }
            }
        });
        final ActorRef ref2 = system.spawn(mailbox -> {
            switch (mailbox.status()) {
                case Success -> {
                    mailbox.sender().post(mailbox.message(), mailbox.self());
                    latch2.countDown();
                    return NextState.Receive;
                }
                case Failure -> {
                    return NextState.Receive;
                }
                default -> {
                    return NextState.Terminate;
                }
            }
        });
        ref1.post("Hello, World!", ref2);

        final boolean result1 = latch1.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        final boolean result2 = latch2.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertTrue(result1, "Actor 1 should have received %d messages".formatted(messageCount));
        assertTrue(result2, "Actor 2 should have received %d messages".formatted(messageCount));
    }

    @Test
    @DisplayName("Should transition states")
    public void shouldTransitionStates() throws InterruptedException {
        final int messageCount = 10;

        final CountDownLatch latch = new CountDownLatch(messageCount);

        final ActorRef ref = system.spawn(mailbox -> switch (mailbox.status()) {
            case Success -> switch (mailbox.message()) {
                case String command -> {
                    if ("receive".equals(command)) {
                        latch.countDown();
                        yield NextState.Receive;
                    }
                    if ("terminate".equals(command)) {
                        yield NextState.Terminate;
                    }
                    throw new RuntimeException("Should not have reached this code");
                }
                default -> throw new RuntimeException("Should not have reached this code");
            };
            default -> throw new RuntimeException("Should not have reached this code");
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

        final boolean result = latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertFalse(result, "Actor should have stopped processing messages");

        final int count = (int) latch.getCount();
        assertEquals(messageCount / 2, count, "Actor should have processed only %d messages".formatted(messageCount / 2));
    }
}