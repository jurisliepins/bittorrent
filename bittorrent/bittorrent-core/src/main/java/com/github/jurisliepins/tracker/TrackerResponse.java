package com.github.jurisliepins.tracker;

import com.github.jurisliepins.BDecoder;
import com.github.jurisliepins.CoreException;
import com.github.jurisliepins.stream.BInputStream;
import com.github.jurisliepins.value.BByteString;
import com.github.jurisliepins.value.BDictionary;
import com.github.jurisliepins.value.BList;
import com.github.jurisliepins.value.BValue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public sealed interface TrackerResponse permits TrackerResponse.Success, TrackerResponse.Failure {
    record Success(
            Long complete,
            Long incomplete,
            Long interval,
            Long minInterval,
            List<InetSocketAddress> peers,
            String trackerId,
            String warningMessage
    ) implements TrackerResponse {
        public Success {
            Objects.requireNonNull(interval, "interval is null");
            Objects.requireNonNull(peers, "peers is null");
        }
    }

    record Failure(String failureReason) implements TrackerResponse {
        public Failure {
            Objects.requireNonNull(failureReason, "failureReason is null");
        }
    }

    static TrackerResponse fromStream(final BInputStream stream) throws IOException {
        return switch (BDecoder.fromStream(stream)) {
            case BDictionary dictionary -> Optional.ofNullable(dictionary.value().get(BByteString.of("failure reason")))
                    .map(val -> (TrackerResponse) new Failure(val.toString(StandardCharsets.UTF_8)))
                    .orElseGet(() -> (TrackerResponse) new Success(
                            Optional.ofNullable(dictionary.value().get(BByteString.of("complete"))).map(TrackerResponse::parseNumber).orElse(null),
                            Optional.ofNullable(dictionary.value().get(BByteString.of("incomplete"))).map(TrackerResponse::parseNumber).orElse(null),
                            Optional.ofNullable(dictionary.value().get(BByteString.of("interval"))).map(TrackerResponse::parseNumber).orElse(null),
                            Optional.ofNullable(dictionary.value().get(BByteString.of("min interval"))).map(TrackerResponse::parseNumber).orElse(null),
                            Optional.ofNullable(dictionary.value().get(BByteString.of("peers"))).map(TrackerResponse::parsePeers).orElse(null),
                            Optional.ofNullable(dictionary.value().get(BByteString.of("tracker id"))).map(TrackerResponse::parseString).orElse(null),
                            Optional.ofNullable(dictionary.value().get(BByteString.of("warning message"))).map(TrackerResponse::parseString).orElse(null)
                    ));

            default -> throw new CoreException("Unexpected decoded response from tracker - BDictionary expected");
        };
    }

    static TrackerResponse fromBytes(final byte[] bytes) throws IOException {
        try (BInputStream stream = new BInputStream(bytes)) {
            return fromStream(stream);
        }
    }

    static TrackerResponse fromString(final String string) throws IOException {
        return fromBytes(string.getBytes(StandardCharsets.ISO_8859_1));
    }

    private static Long parseNumber(final BValue value) {
        return value.toLong();
    }

    private static List<InetSocketAddress> parsePeers(final BValue value) {
        return switch (value) {
            case BByteString peers -> parsePeersFromByteString(peers);
            case BList peers -> parsePeersFromList(peers);
            default -> throw new CoreException("Unexpected peer encoding");
        };
    }

    private static List<InetSocketAddress> parsePeersFromByteString(final BByteString value) {
        final ArrayList<InetSocketAddress> parsed = new ArrayList<>();

        final byte[] b = value.toBytes();
        for (int i = 0; i <= b.length - 6; i += 6) {
            final byte[] addr = Arrays.copyOfRange(b, i, i + 4);
            final byte[] port = Arrays.copyOfRange(b, i + 4, i + 6);
            try {
                parsed.add(new InetSocketAddress(InetAddress.getByAddress(addr), ByteBuffer.wrap(port).getShort()));
            } catch (Exception e) {
                throw new CoreException("Failed to parse compact peer from tracker", e);
            }
        }

        return parsed;
    }

    private static List<InetSocketAddress> parsePeersFromList(final BList value) {
        return value.value()
                .stream()
                .map(peer -> {
                    final Map<BValue, BValue> dict = peer.toBDictionary().value();
                    final BValue addr = dict.get(BByteString.of("ip"));
                    final BValue port = dict.get(BByteString.of("port"));
                    try {
                        return new InetSocketAddress(InetAddress.getByName(addr.toString(StandardCharsets.UTF_8)), port.toInteger());
                    } catch (Exception e) {
                        throw new CoreException("Failed to parse peer from tracker", e);
                    }
                })
                .collect(Collectors.toList());
    }

    private static String parseString(final BValue value) {
        return value.toString(StandardCharsets.UTF_8);
    }
}
