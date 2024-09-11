package com.github.jurisliepins.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.jurisliepins.peer.PeerId;

import java.io.IOException;

public class PeerIdSerializer  extends StdSerializer<PeerId> {
    public PeerIdSerializer(final Class<PeerId> t) {
        super(t);
    }

    @Override
    public void serialize(
            final PeerId peerId,
            final JsonGenerator jsonGenerator,
            final SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(peerId.toString());
    }
}
