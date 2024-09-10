package com.github.jurisliepins.announcer;

import com.github.jurisliepins.AbstractMailboxReceiverTests;
import com.github.jurisliepins.CoreException;
import com.github.jurisliepins.NotificationAwaiter;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.config.Config;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.peer.PeerId;
import com.github.jurisliepins.tracker.TrackerClient;
import com.github.jurisliepins.tracker.TrackerResponse;
import com.github.jurisliepins.types.StatusType;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Announcer mailbox receiver tests")
public final class AnnouncerMailboxReceiverTests extends AbstractMailboxReceiverTests {

    @Test
    @DisplayName("Should announcer start succeed")
    public void shouldAnnouncerStartSucceed() {
        var config = successConfig();
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(2);
        var notifiedRef = system.spawn(awaiter);
        var state = blankState();

        var ref = system.spawn(new AnnouncerMailboxReceiver(config, notifiedRef, state));
        ref.post(AnnouncerCommand.Start.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(2, notifications.size());
        assertEquals(InfoHash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(0)).infoHash());
        assertEquals(StatusType.Started, ((AnnouncerNotification.StatusChanged) notifications.get(0)).status());
        assertEquals(InfoHash.BLANK, ((AnnouncerNotification.PeersReceived) notifications.get(1)).infoHash());
        assertEquals(List.of(), ((AnnouncerNotification.PeersReceived) notifications.get(1)).peers());
    }

    @Test
    @DisplayName("Should announcer start fail")
    public void shouldAnnouncerStartFail() {
        var config = failureConfig();
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(2);
        var notifiedRef = system.spawn(awaiter);
        var state = blankState();

        var ref = system.spawn(new AnnouncerMailboxReceiver(config, notifiedRef, state));
        ref.post(AnnouncerCommand.Start.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(2, notifications.size());
        assertEquals(InfoHash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(0)).infoHash());
        assertEquals(StatusType.Started, ((AnnouncerNotification.StatusChanged) notifications.get(0)).status());
        assertEquals(InfoHash.BLANK, ((AnnouncerNotification.Failure) notifications.get(1)).infoHash());
        assertEquals("Failed to call tracker!", ((AnnouncerNotification.Failure) notifications.get(1)).cause().getMessage());
    }

    @Test
    @DisplayName("Should announcer not start again when already started")
    public void shouldAnnouncerNotStartAgainWhenAlreadyStarted() {
        var config = successConfig();
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(2);
        var notifiedRef = system.spawn(awaiter);
        var state = blankState();

        var ref = system.spawn(new AnnouncerMailboxReceiver(config, notifiedRef, state));
        ref.post(AnnouncerCommand.Start.INSTANCE);
        ref.post(AnnouncerCommand.Start.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(2, notifications.size());
        assertEquals(InfoHash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(0)).infoHash());
        assertEquals(StatusType.Started, ((AnnouncerNotification.StatusChanged) notifications.get(0)).status());
        assertEquals(InfoHash.BLANK, ((AnnouncerNotification.PeersReceived) notifications.get(1)).infoHash());
        assertEquals(List.of(), ((AnnouncerNotification.PeersReceived) notifications.get(1)).peers());
    }

    @Test
    @DisplayName("Should announcer stop succeed")
    public void shouldAnnouncerStopSucceed() {
        var config = successConfig();
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(2);
        var notifiedRef = system.spawn(awaiter);
        var state = blankState();

        var ref = system.spawn(new AnnouncerMailboxReceiver(config, notifiedRef, state));
        ref.post(AnnouncerCommand.Start.INSTANCE);
        ref.post(AnnouncerCommand.Stop.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(2, notifications.size());
        assertEquals(InfoHash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(0)).infoHash());
        assertEquals(StatusType.Started, ((AnnouncerNotification.StatusChanged) notifications.get(0)).status());
        assertEquals(InfoHash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(1)).infoHash());
        assertEquals(StatusType.Stopped, ((AnnouncerNotification.StatusChanged) notifications.get(1)).status());
    }

    @Test
    @DisplayName("Should announcer stop fail")
    public void shouldAnnouncerStopFail() {
        var config = failureConfig();
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(3);
        var notifiedRef = system.spawn(awaiter);
        var state = blankState();

        var ref = system.spawn(new AnnouncerMailboxReceiver(config, notifiedRef, state));
        ref.post(AnnouncerCommand.Start.INSTANCE);
        ref.post(AnnouncerCommand.Stop.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(3, notifications.size());
        assertEquals(InfoHash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(0)).infoHash());
        assertEquals(StatusType.Started, ((AnnouncerNotification.StatusChanged) notifications.get(0)).status());
        assertEquals(InfoHash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(1)).infoHash());
        assertEquals(StatusType.Stopped, ((AnnouncerNotification.StatusChanged) notifications.get(1)).status());
        assertEquals(InfoHash.BLANK, ((AnnouncerNotification.Failure) notifications.get(2)).infoHash());
        assertEquals("Failed to call tracker!", ((AnnouncerNotification.Failure) notifications.get(2)).cause().getMessage());
    }

    @Test
    @DisplayName("Should announcer not stop again when already stopped")
    public void shouldAnnouncerNotStopAgainWhenAlreadyStopped() {
        var config = successConfig();
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(2);
        var notifiedRef = system.spawn(awaiter);
        var state = blankState();

        var ref = system.spawn(new AnnouncerMailboxReceiver(config, notifiedRef, state));
        ref.post(AnnouncerCommand.Start.INSTANCE);
        ref.post(AnnouncerCommand.Stop.INSTANCE);
        ref.post(AnnouncerCommand.Stop.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(2, notifications.size());
        assertEquals(InfoHash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(0)).infoHash());
        assertEquals(StatusType.Started, ((AnnouncerNotification.StatusChanged) notifications.get(0)).status());
        assertEquals(InfoHash.BLANK, ((AnnouncerNotification.StatusChanged) notifications.get(1)).infoHash());
        assertEquals(StatusType.Stopped, ((AnnouncerNotification.StatusChanged) notifications.get(1)).status());
    }

    @Test
    @DisplayName("Should announcer terminate")
    public void shouldAnnouncerTerminate() {
        var config = blankConfig();
        var awaiter = new NotificationAwaiter<AnnouncerNotification>(1);
        var notifiedRef = system.spawn(awaiter);
        var state = blankState();

        var ref = system.spawn(new AnnouncerMailboxReceiver(config, notifiedRef, state));
        ref.post(AnnouncerCommand.Terminate.INSTANCE);

        var notifications = awaiter.awaitResult(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals(1, notifications.size());
        assertEquals(InfoHash.BLANK, ((AnnouncerNotification.Terminated) notifications.get(0)).infoHash());
    }

    private Config successConfig() {
        return new Config(new TrackerClient() {
            @Override
            public TrackerResponse announce(@NonNull final String query) {
                return new TrackerResponse.Success(0L, 0L, 0L, 0L, List.of(), "", "");
            }
        });
    }

    private Config failureConfig() {
        return new Config(new TrackerClient() {
            @Override
            public TrackerResponse announce(@NonNull final String query) {
                throw new CoreException("Failed to call tracker!");
            }
        });
    }

    private Config blankConfig() {
        return new Config(new TrackerClient() {
            @Override
            public TrackerResponse announce(@NonNull final String query) {
                return null;
            }
        });
    }

    private AnnouncerState blankState() {
        return AnnouncerState.builder()
                .status(StatusType.Stopped)
                .infoHash(InfoHash.BLANK)
                .selfPeerId(PeerId.BLANK)
                .announce("")
                .peerCount(0)
                .port(0)
                .downloaded(0L)
                .uploaded(0L)
                .left(0L)
                .build();
    }
}
