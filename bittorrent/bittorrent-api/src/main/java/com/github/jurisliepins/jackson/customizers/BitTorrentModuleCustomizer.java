package com.github.jurisliepins.jackson.customizers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jurisliepins.jackson.modules.BitTorrentModule;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;

@Singleton
public final class BitTorrentModuleCustomizer implements ObjectMapperCustomizer {

    public void customize(final ObjectMapper mapper) {
        mapper.registerModule(new BitTorrentModule());
    }

}
