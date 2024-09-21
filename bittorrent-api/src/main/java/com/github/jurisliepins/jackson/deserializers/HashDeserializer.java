package com.github.jurisliepins.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.jurisliepins.info.Hash;

import java.io.IOException;

public class HashDeserializer extends StdDeserializer<Hash> {
    public HashDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public Hash deserialize(
            final JsonParser jsonParser,
            final DeserializationContext deserializationContext) throws IOException {
        return new Hash(jsonParser.readValueAs(String.class));
    }
}
