package com.github.jurisliepins.tracker;

import com.github.jurisliepins.BDecoder;
import com.github.jurisliepins.CoreException;
import com.github.jurisliepins.stream.BInputStream;
import com.github.jurisliepins.value.BByteString;
import com.github.jurisliepins.value.BDictionary;
import com.github.jurisliepins.value.BList;
import com.github.jurisliepins.value.BValue;
import lombok.NonNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TrackerResponseParser {
    private TrackerResponseParser() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static TrackerResponse fromStream(@NonNull final BInputStream stream) throws IOException {
        return switch (BDecoder.fromStream(stream)) {
            case BDictionary dictionary -> {
                var failureReason = dictionary.value().get(BByteString.of("failure reason"));
                if (failureReason == null) {
                    yield new TrackerResponse.Success(
                            Optional.ofNullable(dictionary.value().get(BByteString.of("complete")))
                                    .map(TrackerResponseParser::parseNumber)
                                    .orElse(null),

                            Optional.ofNullable(dictionary.value().get(BByteString.of("incomplete")))
                                    .map(TrackerResponseParser::parseNumber)
                                    .orElse(null),

                            Optional.ofNullable(dictionary.value().get(BByteString.of("interval")))
                                    .map(TrackerResponseParser::parseNumber)
                                    .orElseThrow(),

                            Optional.ofNullable(dictionary.value().get(BByteString.of("min interval")))
                                    .map(TrackerResponseParser::parseNumber)
                                    .orElse(null),

                            Optional.ofNullable(dictionary.value().get(BByteString.of("peers")))
                                    .map(TrackerResponseParser::parsePeers)
                                    .orElseThrow(),

                            Optional.ofNullable(dictionary.value().get(BByteString.of("tracker id")))
                                    .map(TrackerResponseParser::parseString)
                                    .orElse(null),

                            Optional.ofNullable(dictionary.value().get(BByteString.of("warning message")))
                                    .map(TrackerResponseParser::parseString)
                                    .orElse(null)
                    );
                } else {
                    yield new TrackerResponse.Failure(failureReason.toString(StandardCharsets.UTF_8));
                }
            }

            default -> throw new CoreException("Unexpected decoded response from tracker");
        };
    }

    public static TrackerResponse fromBytes(final byte @NonNull [] bytes) throws IOException {
        try (var stream = new BInputStream(bytes)) {
            return fromStream(stream);
        }
    }

    public static TrackerResponse fromString(@NonNull final String string) throws IOException {
        return fromBytes(string.getBytes(StandardCharsets.ISO_8859_1));
    }

    private static Long parseNumber(@NonNull final BValue value) {
        return value.toLong();
    }

    private static List<InetSocketAddress> parsePeers(@NonNull final BValue value) {
        return switch (value) {
            case BByteString peers -> parsePeers(peers);
            case BList peers -> parsePeers(peers);
            default -> throw new CoreException("Unexpected peer encoding");
        };
    }

    private static List<InetSocketAddress> parsePeers(@NonNull final BByteString value) {
        var parsed = new ArrayList<InetSocketAddress>();

        var b = value.toBytes();
        for (var i = 0; i <= b.length - 6; i += 6) {
            var addr = Arrays.copyOfRange(b, i, i + 4);
            var port = Arrays.copyOfRange(b, i + 4, i + 6);
            try {
                parsed.add(new InetSocketAddress(InetAddress.getByAddress(addr), ByteBuffer.wrap(port).getShort()));
            } catch (Exception e) {
                throw new CoreException("Failed to parse compact peer from tracker", e);
            }
        }

        return parsed;
    }

    private static List<InetSocketAddress> parsePeers(@NonNull final BList value) {
        return value.value()
                .stream()
                .map(peer -> {
                    var dict = peer.toBDictionary().value();
                    var addr = dict.get(BByteString.of("ip"));
                    var port = dict.get(BByteString.of("port"));
                    try {
                        return new InetSocketAddress(InetAddress.getByName(addr.toString(StandardCharsets.UTF_8)), port.toInteger());
                    } catch (Exception e) {
                        throw new CoreException("Failed to parse peer from tracker", e);
                    }
                })
                .collect(Collectors.toList());
    }

    private static String parseString(@NonNull final BValue value) {
        return value.toString(StandardCharsets.UTF_8);
    }
}
