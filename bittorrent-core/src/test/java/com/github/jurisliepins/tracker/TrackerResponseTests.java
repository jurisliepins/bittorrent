package com.github.jurisliepins.tracker;

import com.github.jurisliepins.BConstants;
import com.github.jurisliepins.BEncoder;
import com.github.jurisliepins.stream.BInputStream;
import com.github.jurisliepins.value.BDictionary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import static com.github.jurisliepins.value.BByteString.bstr;
import static com.github.jurisliepins.value.BDictionary.bdict;
import static com.github.jurisliepins.value.BInteger.bint;
import static com.github.jurisliepins.value.BList.blist;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Tracker response tests")
public final class TrackerResponseTests {
    private static final BDictionary PEER_NOT_COMPACT_SUCCESS_RESPONSE = bdict(new HashMap<>() {{
        put(bstr("interval"), bint(1800L));
        put(bstr("peers"), blist(List.of(
                bdict(new HashMap<>() {{
                    put(bstr("ip"), bstr("5.18.147.143"));
                    put(bstr("peer id"), bstr("-TR3000-8bvvpy39oirv"));
                    put(bstr("port"), bint(60446L));
                }})
        )));
    }});

    private static final BDictionary PEER_COMPACT_SUCCESS_RESPONSE = bdict(new HashMap<>() {{
        put(bstr("interval"), bint(1800L));
        put(bstr("peers"), bstr(new byte[]{5, 18, -109, -113, -20, 30}));
    }});

    private static final BDictionary FAILURE_RESPONSE = bdict(new HashMap<>() {{
        put(bstr("failure reason"), bstr("Failure reason!"));
    }});

    @Test
    @DisplayName("Should parse not compact success response from stream succeed")
    public void shouldParseNotCompactSuccessResponseFromStreamSucceed() throws IOException {
        assertSuccessResponse(
                TrackerResponseParser.fromStream(new BInputStream(BEncoder.toStream(PEER_NOT_COMPACT_SUCCESS_RESPONSE).toByteArray()))
        );
    }

    @Test
    @DisplayName("Should parse not compact success response from bytes succeed")
    public void shouldParseNotCompactSuccessResponseFromBytesSucceed() throws IOException {
        assertSuccessResponse(
                TrackerResponseParser.fromBytes(BEncoder.toBytes(PEER_NOT_COMPACT_SUCCESS_RESPONSE))
        );
    }

    @Test
    @DisplayName("Should parse not compact success response from string succeed")
    public void shouldParseNotCompactSuccessResponseFromStringSucceed() throws IOException {
        assertSuccessResponse(
                TrackerResponseParser.fromString(BEncoder.toString(PEER_NOT_COMPACT_SUCCESS_RESPONSE, BConstants.DEFAULT_ENCODING))
        );
    }

    @Test
    @DisplayName("Should parse compact success response from stream succeed")
    public void shouldParseCompactSuccessResponseFromStreamSucceed() throws IOException {
        assertSuccessResponse(
                TrackerResponseParser.fromStream(new BInputStream(BEncoder.toStream(PEER_COMPACT_SUCCESS_RESPONSE).toByteArray()))
        );
    }

    @Test
    @DisplayName("Should parse compact success response from bytes succeed")
    public void shouldParseCompactSuccessResponseFromBytesSucceed() throws IOException {
        assertSuccessResponse(
                TrackerResponseParser.fromBytes(BEncoder.toBytes(PEER_COMPACT_SUCCESS_RESPONSE))
        );
    }

    @Test
    @DisplayName("Should parse compact success response from string succeed")
    public void shouldParseCompactSuccessResponseFromStringSucceed() throws IOException {
        assertSuccessResponse(
                TrackerResponseParser.fromString(BEncoder.toString(PEER_COMPACT_SUCCESS_RESPONSE, BConstants.DEFAULT_ENCODING))
        );
    }

    @Test
    @DisplayName("Should parse failure response from stream succeed")
    public void shouldParseFailureResponseFromStreamSucceed() throws IOException {
        assertFailureResponse(
                TrackerResponseParser.fromStream(new BInputStream(BEncoder.toStream(FAILURE_RESPONSE).toByteArray()))
        );
    }

    @Test
    @DisplayName("Should parse failure response from bytes succeed")
    public void shouldParseFailureResponseFromBytesSucceed() throws IOException {
        assertFailureResponse(
                TrackerResponseParser.fromBytes(BEncoder.toBytes(FAILURE_RESPONSE))
        );
    }

    @Test
    @DisplayName("Should parse failure response from string succeed")
    public void shouldParseFailureReasonFromStringSucceed() throws IOException {
        assertFailureResponse(
                TrackerResponseParser.fromString(BEncoder.toString(FAILURE_RESPONSE, BConstants.DEFAULT_ENCODING))
        );
    }

    private static void assertSuccessResponse(final TrackerResponse response) throws UnknownHostException {
        switch (response) {
            case TrackerResponse.Success success -> {
                assertNull(success.complete());
                assertNull(success.incomplete());
                assertEquals(1800L, success.interval());
                assertNull(success.minInterval());
                assertEquals(List.of(new InetSocketAddress(InetAddress.getByName("5.18.147.143"), 60446)), success.peers());
                assertNull(success.trackerId());
                assertNull(success.warningMessage());
            }
            case TrackerResponse.Failure ignore -> throw new IllegalArgumentException("Should have received success response");
        }
    }

    private static void assertFailureResponse(final TrackerResponse response) {
        switch (response) {
            case TrackerResponse.Success ignore -> throw new IllegalArgumentException("Should have received success response");
            case TrackerResponse.Failure failure -> assertEquals("Failure reason!", failure.failureReason());
        }
    }
}
