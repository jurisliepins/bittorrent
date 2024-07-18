package com.github.jurisliepins.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jurisliepins.jackson.modules.BitTorrentModule;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;

@Singleton
public final class RegisterCustomModuleCustomizer implements ObjectMapperCustomizer {

    public void customize(final ObjectMapper mapper) {
        mapper.registerModule(new BitTorrentModule());
    }

}
