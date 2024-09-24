package com.github.jurisliepins.tracker.url;

import lombok.NonNull;

import static com.github.jurisliepins.tracker.url.UrlEncoding.encode;
import static com.github.jurisliepins.tracker.url.UrlEncoding.requireSafe;

public final class BinaryNamedParameterValue implements NamedParameterValue {

    private final String name;
    private final String value;

    public BinaryNamedParameterValue(@NonNull final String name, final byte @NonNull [] value) {
        this.name = requireSafe(name);
        this.value = encode(value);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String value() {
        return value;
    }
}
