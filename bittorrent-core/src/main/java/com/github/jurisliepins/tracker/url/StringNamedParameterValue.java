package com.github.jurisliepins.tracker.url;

import static com.github.jurisliepins.tracker.url.UrlEncoding.requireSafe;
import static java.util.Objects.requireNonNull;

public final class StringNamedParameterValue implements NamedParameterValue {

    private final String name;
    private final String value;

    public StringNamedParameterValue(final String name, final String value) {
        this.name = requireSafe(name);
        this.value = requireNonNull(value);
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
