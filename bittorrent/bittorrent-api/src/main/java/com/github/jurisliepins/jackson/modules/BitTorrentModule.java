package com.github.jurisliepins.jackson.modules;

import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.jackson.deserializers.InfoHashDeserializer;
import com.github.jurisliepins.jackson.serializers.BitfieldSerializer;
import com.github.jurisliepins.jackson.serializers.InfoHashSerializer;

public class BitTorrentModule extends SimpleModule {

    public BitTorrentModule() {
        super("BitTorrentModule");
    }

    @Override
    public void setupModule(final SetupContext context) {
        final SimpleSerializers serializers = new SimpleSerializers();
        serializers.addSerializer(InfoHash.class, new InfoHashSerializer(InfoHash.class));
        serializers.addSerializer(Bitfield.class, new BitfieldSerializer(Bitfield.class));
        context.addSerializers(serializers);

        final SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(InfoHash.class, new InfoHashDeserializer(InfoHash.class));
        context.addDeserializers(deserializers);
    }
}
