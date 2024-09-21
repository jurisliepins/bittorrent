package com.github.jurisliepins.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.jurisliepins.peer.Id;

import java.io.IOException;

public class IdSerializer extends StdSerializer<Id> {
    public IdSerializer(final Class<Id> t) {
        super(t);
    }

    @Override
    public void serialize(
            final Id id,
            final JsonGenerator jsonGenerator,
            final SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(id.toString());
    }
}
