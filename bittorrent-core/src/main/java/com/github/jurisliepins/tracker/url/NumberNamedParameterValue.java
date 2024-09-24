package com.github.jurisliepins.tracker.url;

import lombok.NonNull;

import static com.github.jurisliepins.tracker.url.UrlEncoding.requireSafe;
import static java.util.Objects.requireNonNull;

public final class NumberNamedParameterValue implements NamedParameterValue {

    private final String name;
    private final String value;

    public NumberNamedParameterValue(@NonNull final String name, @NonNull final Number value) {
        this.name = requireSafe(name);
        this.value = requireNonNull(value).toString();
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
