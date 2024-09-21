package com.github.jurisliepins.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.jurisliepins.info.Hash;

import java.io.IOException;

public class HashSerializer extends StdSerializer<Hash> {
    public HashSerializer(final Class<Hash> t) {
        super(t);
    }

    @Override
    public void serialize(
            final Hash infoHash,
            final JsonGenerator jsonGenerator,
            final SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(infoHash.toString());
    }
}
