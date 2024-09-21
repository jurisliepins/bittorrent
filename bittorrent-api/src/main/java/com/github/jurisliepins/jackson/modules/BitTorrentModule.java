package com.github.jurisliepins.jackson.modules;

import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.info.Hash;
import com.github.jurisliepins.jackson.deserializers.HashDeserializer;
import com.github.jurisliepins.jackson.serializers.BitfieldSerializer;
import com.github.jurisliepins.jackson.serializers.HashSerializer;
import com.github.jurisliepins.jackson.serializers.IdSerializer;
import com.github.jurisliepins.peer.Id;

public class BitTorrentModule extends SimpleModule {

    public BitTorrentModule() {
        super("BitTorrentModule");
    }

    @Override
    public void setupModule(final SetupContext context) {
        var serializers = new SimpleSerializers();
        serializers.addSerializer(Hash.class, new HashSerializer(Hash.class));
        serializers.addSerializer(Id.class, new IdSerializer(Id.class));
        serializers.addSerializer(Bitfield.class, new BitfieldSerializer(Bitfield.class));
        context.addSerializers(serializers);

        var deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(Hash.class, new HashDeserializer(Hash.class));
        context.addDeserializers(deserializers);
    }
}
