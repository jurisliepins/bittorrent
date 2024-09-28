package com.github.jurisliepins.announcer;

import com.github.jurisliepins.CoreException;
import com.github.jurisliepins.MailboxReceiverTests;
import com.github.jurisliepins.NotificationAwaiter;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.common.StatusType;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.info.Hash;
import com.github.jurisliepins.peer.Id;
import com.github.jurisliepins.tracker.TrackerResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Announcer mailbox receiver tests")
public final class AnnouncerMailboxReceiverTests extends MailboxReceiverTests {
    @Test
    @DisplayName("Should announcer start succeed with success response")
    public void shouldAnnouncerStartSucceedWithSuccessResponse() {
        var messageCount = 2;
        var context = Context.defaultContext()
                .withIo(Context.defaultContext()
                                .io()
                                .withTrackerClient(query -> new TrackerResponse.Success(0L, 0L, 0L, 0L, List.of(), "", "")));
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(messageCount);
        var notifiedRef = system.spawn(awaiter);
        var state = AnnouncerState.builder()
                .status(StatusType.Stopped)
                .infoHash(Hash.BLANK)
                .selfId(Id.BLANK)
                .announce("")
                .announceList(new String[][]{})
                .peerCount(0)
                .port(0)
                .intervalSeconds(1)
                .downloaded(0L)
                .uploaded(0L)
                .left(0L)
                .notifiedRef(notifiedRef)
                .build();

        var ref = system.spawn(new AnnouncerMailboxReceiver(context, state));
        ref.post(AnnouncerCommand.Start.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(messageCount, notifications.size());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(0)).infoHash());
        assertEquals(StatusType.Started, ((AnnouncerNotification.StatusChanged) notifications.get(0)).status());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.PeersReceived) notifications.get(1)).infoHash());
        assertEquals(List.of(), ((AnnouncerNotification.PeersReceived) notifications.get(1)).peers());
    }

    @Test
    @DisplayName("Should announcer start succeed with failure response")
    public void shouldAnnouncerStartSucceedWithFailureResponse() {
        var messageCount = 1;
        var context = Context.defaultContext()
                .withIo(Context.defaultContext()
                                .io()
                                .withTrackerClient(query -> new TrackerResponse.Failure("Failure!")));
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(messageCount);
        var notifiedRef = system.spawn(awaiter);
        var state = AnnouncerState.builder()
                .status(StatusType.Stopped)
                .infoHash(Hash.BLANK)
                .selfId(Id.BLANK)
                .announce("")
                .announceList(new String[][]{})
                .peerCount(0)
                .port(0)
                .intervalSeconds(1)
                .downloaded(0L)
                .uploaded(0L)
                .left(0L)
                .notifiedRef(notifiedRef)
                .build();

        var ref = system.spawn(new AnnouncerMailboxReceiver(context, state));
        ref.post(AnnouncerCommand.Start.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(messageCount, notifications.size());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(0)).infoHash());
        assertEquals(StatusType.Started, ((AnnouncerNotification.StatusChanged) notifications.get(0)).status());
    }

    @Test
    @DisplayName("Should announcer start fail")
    public void shouldAnnouncerStartFail() {
        var messageCount = 2;
        var context = Context.defaultContext()
                .withIo(Context.defaultContext()
                                .io()
                                .withTrackerClient(query -> {
                                    throw new CoreException("Failed to call tracker!");
                                }));
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(messageCount);
        var notifiedRef = system.spawn(awaiter);
        var state = AnnouncerState.builder()
                .status(StatusType.Stopped)
                .infoHash(Hash.BLANK)
                .selfId(Id.BLANK)
                .announce("")
                .announceList(new String[][]{})
                .peerCount(0)
                .port(0)
                .intervalSeconds(1)
                .downloaded(0L)
                .uploaded(0L)
                .left(0L)
                .notifiedRef(notifiedRef)
                .build();

        var ref = system.spawn(new AnnouncerMailboxReceiver(context, state));
        ref.post(AnnouncerCommand.Start.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(messageCount, notifications.size());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(0)).infoHash());
        assertEquals(StatusType.Started, ((AnnouncerNotification.StatusChanged) notifications.get(0)).status());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.Failure) notifications.get(1)).infoHash());
        assertEquals("Failed to call tracker!", ((AnnouncerNotification.Failure) notifications.get(1)).cause().getMessage());
    }

    @Test
    @DisplayName("Should announcer not start again when already started")
    public void shouldAnnouncerNotStartAgainWhenAlreadyStarted() {
        var messageCount = 2;
        var context = Context.defaultContext()
                .withIo(Context.defaultContext()
                                .io()
                                .withTrackerClient(query -> new TrackerResponse.Success(0L, 0L, 0L, 0L, List.of(), "", "")));
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(messageCount);
        var notifiedRef = system.spawn(awaiter);
        var state = AnnouncerState.builder()
                .status(StatusType.Stopped)
                .infoHash(Hash.BLANK)
                .selfId(Id.BLANK)
                .announce("")
                .announceList(new String[][]{})
                .peerCount(0)
                .port(0)
                .intervalSeconds(1)
                .downloaded(0L)
                .uploaded(0L)
                .left(0L)
                .notifiedRef(notifiedRef)
                .build();

        var ref = system.spawn(new AnnouncerMailboxReceiver(context, state));
        ref.post(AnnouncerCommand.Start.INSTANCE);
        ref.post(AnnouncerCommand.Start.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(messageCount, notifications.size());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(0)).infoHash());
        assertEquals(StatusType.Started, ((AnnouncerNotification.StatusChanged) notifications.get(0)).status());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.PeersReceived) notifications.get(1)).infoHash());
        assertEquals(List.of(), ((AnnouncerNotification.PeersReceived) notifications.get(1)).peers());
    }

    @Test
    @DisplayName("Should announcer stop succeed")
    public void shouldAnnouncerStopSucceed() {
        var messageCount = 2;
        var context = Context.defaultContext()
                .withIo(Context.defaultContext()
                                .io()
                                .withTrackerClient(query -> new TrackerResponse.Success(0L, 0L, 0L, 0L, List.of(), "", "")));
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(messageCount);
        var notifiedRef = system.spawn(awaiter);
        var state = AnnouncerState.builder()
                .status(StatusType.Stopped)
                .infoHash(Hash.BLANK)
                .selfId(Id.BLANK)
                .announce("")
                .announceList(new String[][]{})
                .peerCount(0)
                .port(0)
                .intervalSeconds(1)
                .downloaded(0L)
                .uploaded(0L)
                .left(0L)
                .notifiedRef(notifiedRef)
                .build();

        var ref = system.spawn(new AnnouncerMailboxReceiver(context, state));
        ref.post(AnnouncerCommand.Start.INSTANCE);
        ref.post(AnnouncerCommand.Stop.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(messageCount, notifications.size());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(0)).infoHash());
        assertEquals(StatusType.Started, ((AnnouncerNotification.StatusChanged) notifications.get(0)).status());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(1)).infoHash());
        assertEquals(StatusType.Stopped, ((AnnouncerNotification.StatusChanged) notifications.get(1)).status());
    }

    @Test
    @DisplayName("Should announcer stop fail")
    public void shouldAnnouncerStopFail() {
        var messageCount = 3;
        var context = Context.defaultContext()
                .withIo(Context.defaultContext()
                                .io()
                                .withTrackerClient(query -> {
                                    throw new CoreException("Failed to call tracker!");
                                }));
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(messageCount);
        var notifiedRef = system.spawn(awaiter);
        var state = AnnouncerState.builder()
                .status(StatusType.Stopped)
                .infoHash(Hash.BLANK)
                .selfId(Id.BLANK)
                .announce("")
                .announceList(new String[][]{})
                .peerCount(0)
                .port(0)
                .intervalSeconds(1)
                .downloaded(0L)
                .uploaded(0L)
                .left(0L)
                .notifiedRef(notifiedRef)
                .build();

        var ref = system.spawn(new AnnouncerMailboxReceiver(context, state));
        ref.post(AnnouncerCommand.Start.INSTANCE);
        ref.post(AnnouncerCommand.Stop.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(messageCount, notifications.size());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(0)).infoHash());
        assertEquals(StatusType.Started, ((AnnouncerNotification.StatusChanged) notifications.get(0)).status());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(1)).infoHash());
        assertEquals(StatusType.Stopped, ((AnnouncerNotification.StatusChanged) notifications.get(1)).status());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.Failure) notifications.get(2)).infoHash());
        assertEquals("Failed to call tracker!", ((AnnouncerNotification.Failure) notifications.get(2)).cause().getMessage());
    }

    @Test
    @DisplayName("Should announcer not stop again when already stopped")
    public void shouldAnnouncerNotStopAgainWhenAlreadyStopped() {
        var messageCount = 2;
        var context = Context.defaultContext()
                .withIo(Context.defaultContext()
                                .io()
                                .withTrackerClient(query -> new TrackerResponse.Success(0L, 0L, 0L, 0L, List.of(), "", "")));
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(messageCount);
        var notifiedRef = system.spawn(awaiter);
        var state = AnnouncerState.builder()
                .status(StatusType.Stopped)
                .infoHash(Hash.BLANK)
                .selfId(Id.BLANK)
                .announce("")
                .announceList(new String[][]{})
                .peerCount(0)
                .port(0)
                .intervalSeconds(1)
                .downloaded(0L)
                .uploaded(0L)
                .left(0L)
                .notifiedRef(notifiedRef)
                .build();

        var ref = system.spawn(new AnnouncerMailboxReceiver(context, state));
        ref.post(AnnouncerCommand.Start.INSTANCE);
        ref.post(AnnouncerCommand.Stop.INSTANCE);
        ref.post(AnnouncerCommand.Stop.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(messageCount, notifications.size());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(0)).infoHash());
        assertEquals(StatusType.Started, ((AnnouncerNotification.StatusChanged) notifications.get(0)).status());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(1)).infoHash());
        assertEquals(StatusType.Stopped, ((AnnouncerNotification.StatusChanged) notifications.get(1)).status());
    }

    @Test
    @DisplayName("Should announcer terminate")
    public void shouldAnnouncerTerminate() {
        var messageCount = 1;
        var context = Context.defaultContext();
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(messageCount);
        var notifiedRef = system.spawn(awaiter);
        var state = AnnouncerState.builder()
                .status(StatusType.Stopped)
                .infoHash(Hash.BLANK)
                .selfId(Id.BLANK)
                .announce("")
                .announceList(new String[][]{})
                .peerCount(0)
                .port(0)
                .intervalSeconds(1)
                .downloaded(0L)
                .uploaded(0L)
                .left(0L)
                .notifiedRef(notifiedRef)
                .build();

        var ref = system.spawn(new AnnouncerMailboxReceiver(context, state));
        ref.post(AnnouncerCommand.Terminate.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(messageCount, notifications.size());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.Terminated) notifications.get(0)).infoHash());
    }

    @Test
    @DisplayName("Should announcer schedule re-announce")
    public void shouldAnnouncerScheduleReAnnounce() {
        var messageCount = 3;
        var context = Context.defaultContext()
                .withIo(Context.defaultContext()
                                .io()
                                .withTrackerClient(query -> new TrackerResponse.Success(0L, 0L, 0L, 0L, List.of(), "", "")));
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(messageCount);
        var notifiedRef = system.spawn(awaiter);
        var state = AnnouncerState.builder()
                .status(StatusType.Stopped)
                .infoHash(Hash.BLANK)
                .selfId(Id.BLANK)
                .announce("")
                .announceList(new String[][]{})
                .peerCount(0)
                .port(0)
                .intervalSeconds(1)
                .downloaded(0L)
                .uploaded(0L)
                .left(0L)
                .notifiedRef(notifiedRef)
                .build();

        var ref = system.spawn(new AnnouncerMailboxReceiver(context, state));
        ref.post(AnnouncerCommand.Start.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(messageCount, notifications.size());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(0)).infoHash());
        assertEquals(StatusType.Started, ((AnnouncerNotification.StatusChanged) notifications.get(0)).status());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.PeersReceived) notifications.get(1)).infoHash());
        assertEquals(List.of(), ((AnnouncerNotification.PeersReceived) notifications.get(1)).peers());
        assertEquals(Hash.BLANK, ((AnnouncerNotification.PeersReceived) notifications.get(2)).infoHash());
        assertEquals(List.of(), ((AnnouncerNotification.PeersReceived) notifications.get(2)).peers());
    }
}
