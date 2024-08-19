package com.github.jurisliepins.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.jurisliepins.bitfield.Bitfield;

import java.io.IOException;

public class BitfieldSerializer extends StdSerializer<Bitfield> {
    public BitfieldSerializer(final Class<Bitfield> t) {
        super(t);
    }

    @Override
    public void serialize(
            final Bitfield infoHash,
            final JsonGenerator jsonGenerator,
            final SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        jsonGenerator.writeEndArray();
    }
}
