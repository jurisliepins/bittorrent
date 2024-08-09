package com.github.jurisliepins.tracker.url;

import java.util.Objects;

public final class BinaryNamedParameterValue implements NamedParameterValue {

    private final String name;
    private final String value;

    public BinaryNamedParameterValue(final String name, final byte[] value) {
        this.name = UrlEncoding.requireSafe(Objects.requireNonNull(name, "name is null"));
        this.value = UrlEncoding.encode(Objects.requireNonNull(value, "value is null"));
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
