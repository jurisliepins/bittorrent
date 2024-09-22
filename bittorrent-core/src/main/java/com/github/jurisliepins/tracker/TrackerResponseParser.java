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
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.jurisliepins.value.BByteString.bstr;

@UtilityClass
public final class TrackerResponseParser {
    public static TrackerResponse fromStream(@NonNull final BInputStream stream) throws IOException {
        return switch (BDecoder.fromStream(stream)) {
            case BDictionary dictionary -> {
                var failureReason = dictionary.value().get(bstr("failure reason"));
                if (failureReason == null) {
                    yield new TrackerResponse.Success(
                            Optional.ofNullable(dictionary.value().get(bstr("complete")))
                                    .map(TrackerResponseParser::parseNumber)
                                    .orElse(null),

                            Optional.ofNullable(dictionary.value().get(bstr("incomplete")))
                                    .map(TrackerResponseParser::parseNumber)
                                    .orElse(null),

                            Optional.ofNullable(dictionary.value().get(bstr("interval")))
                                    .map(TrackerResponseParser::parseNumber)
                                    .orElseThrow(),

                            Optional.ofNullable(dictionary.value().get(bstr("min interval")))
                                    .map(TrackerResponseParser::parseNumber)
                                    .orElse(null),

                            Optional.ofNullable(dictionary.value().get(bstr("peers")))
                                    .map(TrackerResponseParser::parsePeers)
                                    .orElseThrow(),

                            Optional.ofNullable(dictionary.value().get(bstr("tracker id")))
                                    .map(TrackerResponseParser::parseString)
                                    .orElse(null),

                            Optional.ofNullable(dictionary.value().get(bstr("warning message")))
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
        final var length = 6;

        var parsed = new ArrayList<InetSocketAddress>();

        var b = value.toBytes();
        for (var i = 0; i <= b.length - length; i += length) {
            var addr = Arrays.copyOfRange(b, i, i + (length - 2));
            var port = Arrays.copyOfRange(b, i + (length - 2), i + length);
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
