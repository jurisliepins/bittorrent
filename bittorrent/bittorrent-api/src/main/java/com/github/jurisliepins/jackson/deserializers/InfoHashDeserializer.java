package com.github.jurisliepins.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.jurisliepins.info.InfoHash;

import java.io.IOException;

public class InfoHashDeserializer extends StdDeserializer<InfoHash> {
    public InfoHashDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public InfoHash deserialize(
            final JsonParser jsonParser,
            final DeserializationContext deserializationContext) throws
            IOException {
        return new InfoHash(jsonParser.readValueAs(String.class));
    }
}
