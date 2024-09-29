package com.github.jurisliepins.tracker;

import com.github.jurisliepins.BDecoder;
import com.github.jurisliepins.CoreException;
import com.github.jurisliepins.stream.BInputStream;
import com.github.jurisliepins.value.BByteString;
import com.github.jurisliepins.value.BDictionary;
import com.github.jurisliepins.value.BList;
import com.github.jurisliepins.value.BValue;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.jurisliepins.value.BByteString.bstr;

@UtilityClass
public final class TrackerResponseParser {
    public static TrackerResponse fromStream(@NonNull final BInputStream stream) throws IOException {
        return switch (BDecoder.fromStream(stream)) {
            case BDictionary dictionary -> {
                var failureReason = failureReason(dictionary);
                if (failureReason == null) {
                    yield new TrackerResponse.Success(
                            complete(dictionary),
                            incomplete(dictionary),
                            interval(dictionary),
                            minInterval(dictionary),
                            peers(dictionary),
                            trackerId(dictionary),
                            warningMessage(dictionary));
                } else {
                    yield new TrackerResponse.Failure(failureReason);
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

    private static String failureReason(final BDictionary dictionary) {
        return Optional.ofNullable(dictionary.value().get(bstr("failure reason")))
                .map(TrackerResponseParser::parseString)
                .orElse(null);
    }

    private static Long complete(final BDictionary dictionary) {
        return Optional.ofNullable(dictionary.value().get(bstr("complete")))
                .map(TrackerResponseParser::parseNumber)
                .orElse(null);
    }

    private static Long incomplete(final BDictionary dictionary) {
        return Optional.ofNullable(dictionary.value().get(bstr("incomplete")))
                .map(TrackerResponseParser::parseNumber)
                .orElse(null);
    }

    private static Long interval(final BDictionary dictionary) {
        return Optional.ofNullable(dictionary.value().get(bstr("interval")))
                .map(TrackerResponseParser::parseNumber)
                .orElseThrow();
    }

    private static Long minInterval(final BDictionary dictionary) {
        return Optional.ofNullable(dictionary.value().get(bstr("min interval")))
                .map(TrackerResponseParser::parseNumber)
                .orElse(null);
    }

    private static List<InetSocketAddress> peers(final BDictionary dictionary) {
        return Optional.ofNullable(dictionary.value().get(bstr("peers")))
                .map(TrackerResponseParser::parsePeers)
                .orElseThrow();
    }

    private static String trackerId(final BDictionary dictionary) {
        return Optional.ofNullable(dictionary.value().get(bstr("tracker id")))
                .map(TrackerResponseParser::parseString)
                .orElse(null);
    }

    private static String warningMessage(final BDictionary dictionary) {
        return Optional.ofNullable(dictionary.value().get(bstr("warning message")))
                .map(TrackerResponseParser::parseString)
                .orElse(null);
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

    private static final int LENGTH = 6;
    private static final int OFFSET0 = 0;
    private static final int OFFSET1 = 1;
    private static final int OFFSET2 = 2;
    private static final int OFFSET3 = 3;
    private static final int OFFSET4 = 4;
    private static final int OFFSET5 = 5;

    private static List<InetSocketAddress> parsePeers(@NonNull final BByteString value) {
        var bytes = value.toBytes();

        return IntStream.range(0, bytes.length / LENGTH)
                .mapToObj(idx -> {
                    var addr = new byte[]{
                            bytes[(idx * LENGTH) + OFFSET0],
                            bytes[(idx * LENGTH) + OFFSET1],
                            bytes[(idx * LENGTH) + OFFSET2],
                            bytes[(idx * LENGTH) + OFFSET3]
                    };
                    var port = new byte[]{
                            0,
                            0,
                            bytes[(idx * LENGTH) + OFFSET4],
                            bytes[(idx * LENGTH) + OFFSET5]
                    };
                    try {
                        return new InetSocketAddress(InetAddress.getByAddress(addr), ByteBuffer.wrap(port).getInt());
                    } catch (UnknownHostException e) {
                        throw new CoreException("Failed to parse compact peer from tracker", e);
                    }
                })
                .collect(Collectors.toList());
    }

    private static List<InetSocketAddress> parsePeers(@NonNull final BList value) {
        return value.value()
                .stream()
                .map(peer -> {
                    var dict = peer.toBDictionary().value();
                    var addr = dict.get(bstr("ip"));
                    var port = dict.get(bstr("port"));
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
