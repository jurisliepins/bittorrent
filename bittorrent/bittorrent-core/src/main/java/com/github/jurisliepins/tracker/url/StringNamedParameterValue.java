package com.github.jurisliepins.tracker.url;

import java.util.Objects;

public final class StringNamedParameterValue implements NamedParameterValue {

    private final String name;
    private final String value;

    public StringNamedParameterValue(final String name, final String value) {
        this.name = UrlEncoding.requireSafe(Objects.requireNonNull(name, "name is null"));
        this.value = Objects.requireNonNull(value, "value is null");
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
