package com.github.jurisliepins.tracker.url;

import java.util.Objects;

public final class NumberNamedParameterValue implements NamedParameterValue {

    private final String name;
    private final String value;

    public NumberNamedParameterValue(final String name, final Number value) {
        this.name = UrlEncoding.requireSafe(Objects.requireNonNull(name, "name is null"));
        this.value = Objects.requireNonNull(value, "value is null").toString();
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
