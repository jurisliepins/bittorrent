package com.github.jurisliepins.tracker.url;

import lombok.NonNull;

public final class BinaryNamedParameterValue implements NamedParameterValue {

    private final String name;
    private final String value;

    public BinaryNamedParameterValue(@NonNull final String name, final byte @NonNull [] value) {
        this.name = UrlEncoding.requireSafe(name);
        this.value = UrlEncoding.encode(value);
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
