package com.github.jurisliepins.handshake;

import com.github.jurisliepins.info.Hash;
import com.github.jurisliepins.peer.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.jurisliepins.handshake.Handshake.DEFAULT_PROTOCOL_BYTES;
import static com.github.jurisliepins.handshake.Handshake.DEFAULT_RESERVED_BYTES;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@DisplayName("Handshake tests")
public class HandshakeTests {
    private static final Hash INFO_HASH =  new Hash("494179714a6cd627239dfededf2de9ef994caf03");
    private static final Id PEER_ID = new Id("-XX0000-000000000000");

    @Test
    @DisplayName("Should create default handshake")
    public void shouldCreateDefaultHandshake() {
        var handshake = Handshake.createDefault(INFO_HASH.toByteArray(), PEER_ID.toByteArray());
        assertArrayEquals(DEFAULT_PROTOCOL_BYTES, handshake.protocol());
        assertArrayEquals(DEFAULT_RESERVED_BYTES, handshake.reserved());
        assertArrayEquals(INFO_HASH.toByteArray(), handshake.infoHash());
        assertArrayEquals(PEER_ID.toByteArray(), handshake.peerId());
    }

}
