package com.github.jurisliepins.tracker.url;

import lombok.NonNull;

public final class NumberNamedParameterValue implements NamedParameterValue {

    private final String name;
    private final String value;

    public NumberNamedParameterValue(@NonNull final String name, @NonNull final Number value) {
        this.name = UrlEncoding.requireSafe(name);
        this.value = value.toString();
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
