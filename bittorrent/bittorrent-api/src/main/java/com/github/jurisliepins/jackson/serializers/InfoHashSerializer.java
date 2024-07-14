package com.github.jurisliepins.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.jurisliepins.info.InfoHash;

import java.io.IOException;

public class InfoHashSerializer extends StdSerializer<InfoHash> {
    public InfoHashSerializer(final Class<InfoHash> t) {
        super(t);
    }

    @Override
    public void serialize(
            final InfoHash infoHash,
            final JsonGenerator jsonGenerator,
            final SerializerProvider serializerProvider) throws
            IOException {
        jsonGenerator.writeString(infoHash.toString());
    }
}
