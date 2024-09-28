package com.github.jurisliepins.tracker;

import com.github.jurisliepins.info.Hash;
import com.github.jurisliepins.peer.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tracker request tests")
public final class TrackerRequestTests {
    @Test
    @DisplayName("Should build tracker announce started request succeed")
    public void shouldBuildTrackerAnnounceStartedRequestSucceed() {
        var request = TrackerRequest.builder("http://tracker.com")
                .infoHash(new Hash("aa171ca77f14f55d6ec23d7e9541b7791e6c383c"))
                .peerId(new Id("-BX0001-000000000000"))
                .port(6881)
                .downloaded(0L)
                .uploaded(0L)
                .left(0L)
                .event(Optional.of(TrackerEventType.Started))
                .compact(1)
                .noPeerId(1)
                .numWant(30)
                .toQuery();
        assertEquals(
                "http://tracker.com?" +
                        "info_hash=%AA%17%1C%A7%7F%14%F5%5Dn%C2%3D~%95A%B7y%1El%38%3C&" +
                        "peer_id=-BX0001-000000000000&" +
                        "port=6881&" +
                        "downloaded=0&" +
                        "uploaded=0&" +
                        "left=0&" +
                        "event=started&" +
                        "compact=1&" +
                        "no_peer_id=1&" +
                        "numwant=30",
                request
        );
    }

    @Test
    @DisplayName("Should build tracker announce stopped request succeed")
    public void shouldBuildTrackerAnnounceStoppedRequestSucceed() {
        var request = TrackerRequest.builder("http://tracker.com")
                .infoHash(new Hash("aa171ca77f14f55d6ec23d7e9541b7791e6c383c"))
                .peerId(new Id("-BX0001-000000000000"))
                .port(6881)
                .downloaded(0L)
                .uploaded(0L)
                .left(0L)
                .event(Optional.of(TrackerEventType.Stopped))
                .compact(1)
                .noPeerId(1)
                .numWant(30)
                .toQuery();
        assertEquals(
                "http://tracker.com?" +
                        "info_hash=%AA%17%1C%A7%7F%14%F5%5Dn%C2%3D~%95A%B7y%1El%38%3C&" +
                        "peer_id=-BX0001-000000000000&" +
                        "port=6881&" +
                        "downloaded=0&" +
                        "uploaded=0&" +
                        "left=0&" +
                        "event=stopped&" +
                        "compact=1&" +
                        "no_peer_id=1&" +
                        "numwant=30",
                request
        );
    }

    @Test
    @DisplayName("Should build tracker announce completed request succeed")
    public void shouldBuildTrackerAnnounceCompletedRequestSucceed() {
        var request = TrackerRequest.builder("http://tracker.com")
                .infoHash(new Hash("aa171ca77f14f55d6ec23d7e9541b7791e6c383c"))
                .peerId(new Id("-BX0001-000000000000"))
                .port(6881)
                .downloaded(0L)
                .uploaded(0L)
                .left(0L)
                .event(Optional.of(TrackerEventType.Completed))
                .compact(1)
                .noPeerId(1)
                .numWant(30)
                .toQuery();
        assertEquals(
                "http://tracker.com?" +
                        "info_hash=%AA%17%1C%A7%7F%14%F5%5Dn%C2%3D~%95A%B7y%1El%38%3C&" +
                        "peer_id=-BX0001-000000000000&" +
                        "port=6881&" +
                        "downloaded=0&" +
                        "uploaded=0&" +
                        "left=0&" +
                        "event=completed&" +
                        "compact=1&" +
                        "no_peer_id=1&" +
                        "numwant=30",
                request
        );
    }

    @Test
    @DisplayName("Should build tracker re-announce request succeed")
    public void shouldBuildTrackerReAnnounceRequestSucceed() {
        var request = TrackerRequest.builder("http://tracker.com")
                .infoHash(new Hash("aa171ca77f14f55d6ec23d7e9541b7791e6c383c"))
                .peerId(new Id("-BX0001-000000000000"))
                .port(6881)
                .downloaded(0L)
                .uploaded(0L)
                .left(0L)
                .event(Optional.empty())
                .compact(1)
                .noPeerId(1)
                .numWant(30)
                .toQuery();
        assertEquals(
                "http://tracker.com?" +
                        "info_hash=%AA%17%1C%A7%7F%14%F5%5Dn%C2%3D~%95A%B7y%1El%38%3C&" +
                        "peer_id=-BX0001-000000000000&" +
                        "port=6881&" +
                        "downloaded=0&" +
                        "uploaded=0&" +
                        "left=0&" +
                        "compact=1&" +
                        "no_peer_id=1&" +
                        "numwant=30",
                request
        );
    }
}
