package com.github.jurisliepins.tracker.url;

public final class StringNamedParameterValue implements NamedParameterValue {

    private final String name;
    private final String value;

    public StringNamedParameterValue(final String name, final String value) {
        this.name = UrlEncoding.requireSafe(name);
        this.value = value;
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
