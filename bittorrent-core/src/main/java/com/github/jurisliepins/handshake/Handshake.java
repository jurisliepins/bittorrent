package com.github.jurisliepins.handshake;

import com.github.jurisliepins.info.Hash;
import com.github.jurisliepins.peer.Id;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;

public record Handshake(
        byte @NonNull [] protocol,
        byte @NonNull [] reserved,
        byte @NonNull [] infoHash,
        byte @NonNull [] peerId
) {
    public static final int RESERVED_LENGTH = 8;
    public static final int INFO_HASH_LENGTH = 20;
    public static final int PEER_ID_LENGTH = 20;

    public static final byte[] DEFAULT_PROTOCOL_BYTES = "BitTorrent protocol".getBytes(StandardCharsets.US_ASCII);
    public static final byte[] DEFAULT_RESERVED_BYTES = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

    public static Handshake createDefault(final byte @NonNull [] infoHash, final byte @NonNull [] peerId) {
        return new Handshake(DEFAULT_PROTOCOL_BYTES, DEFAULT_RESERVED_BYTES, infoHash, peerId);
    }

    @Override
    public String toString() {
        return "Handshake[protocol='%s', reserved='%s', infoHash='%s', peerId='%s']"
                .formatted(new String(protocol, StandardCharsets.US_ASCII),
                           new String(reserved, StandardCharsets.US_ASCII),
                           new Hash(infoHash),
                           new Id(peerId));
    }
}
