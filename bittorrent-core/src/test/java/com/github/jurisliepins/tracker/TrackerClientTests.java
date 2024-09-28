package com.github.jurisliepins.tracker;

import com.github.jurisliepins.BEncoder;
import com.github.jurisliepins.info.Hash;
import com.github.jurisliepins.peer.Id;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.github.jurisliepins.value.BByteString.bstr;
import static com.github.jurisliepins.value.BDictionary.bdict;
import static com.github.jurisliepins.value.BInteger.bint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Tracker client tests")
public final class TrackerClientTests {

    private HttpServer successTracker;
    private HttpServer failureTracker;

    private TrackerClientImpl trackerClient;

    @BeforeEach
    void setUp() throws IOException {
        //
        successTracker = HttpServer.create(new InetSocketAddress(8000), 0);
        successTracker.createContext("/announce", exchange -> {
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody()
                    .write(BEncoder.toBytes(
                                   bdict(new HashMap<>() {{
                                       put(bstr("interval"), bint(1800L));
                                       put(bstr("peers"), bstr(new byte[]{5, 18, -109, -113, -20, 30}));
                                   }})
                           )
                    );
            exchange.getResponseBody().close();
        });
        successTracker.start();
        //
        failureTracker = HttpServer.create(new InetSocketAddress(9000), 0);
        failureTracker.createContext("/announce", exchange -> {
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody()
                    .write(BEncoder.toBytes(
                                   bdict(new HashMap<>() {{
                                       put(bstr("failure reason"), bstr("Failure reason!"));
                                   }})
                           )
                    );
            exchange.getResponseBody().close();
        });
        failureTracker.start();
        //
        trackerClient = new TrackerClientImpl();
    }

    @AfterEach
    void tearDown() {
        trackerClient.close();
        successTracker.stop(0);
        failureTracker.stop(0);
    }

    @Test
    @DisplayName("Should announce succeed with success response")
    public void shouldAnnounceSucceedWithSuccessResponse() throws IOException {
        switch (trackerClient.announce(
                TrackerRequest.builder("http://localhost:8000/announce")
                        .infoHash(Hash.BLANK)
                        .peerId(Id.BLANK)
                        .port(6881)
                        .downloaded(0L)
                        .uploaded(0L)
                        .left(0L)
                        .event(Optional.of(TrackerEventType.Started))
                        .compact(1)
                        .noPeerId(1)
                        .numWant(30)
                        .toQuery())) {
            case TrackerResponse.Success success -> {
                assertNull(success.complete());
                assertNull(success.incomplete());
                assertEquals(1800L, success.interval());
                assertNull(success.minInterval());
                assertEquals(List.of(new InetSocketAddress(InetAddress.getByName("5.18.147.143"), 60446)), success.peers());
                assertNull(success.trackerId());
                assertNull(success.warningMessage());
            }
            case TrackerResponse.Failure ignored -> throw new IllegalArgumentException("Should have received success response");
        }
    }

    @Test
    @DisplayName("Should announce succeed with failure response")
    public void shouldAnnounceSucceedWithFailureResponse() {
        switch (trackerClient.announce(
                TrackerRequest.builder("http://localhost:9000/announce")
                        .infoHash(Hash.BLANK)
                        .peerId(Id.BLANK)
                        .port(6881)
                        .downloaded(0L)
                        .uploaded(0L)
                        .left(0L)
                        .event(Optional.of(TrackerEventType.Started))
                        .compact(1)
                        .noPeerId(1)
                        .numWant(30)
                        .toQuery())) {
            case TrackerResponse.Success ignored -> throw new IllegalArgumentException("Should have received success response");
            case TrackerResponse.Failure failure -> assertEquals("Failure reason!", failure.failureReason());
        }
    }
}
