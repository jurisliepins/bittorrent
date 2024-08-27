package com.github.jurisliepins.tracker;

import com.github.jurisliepins.tracker.url.BinaryNamedParameterValue;
import com.github.jurisliepins.tracker.url.NamedParameterValue;
import com.github.jurisliepins.tracker.url.NumberNamedParameterValue;
import com.github.jurisliepins.tracker.url.StringNamedParameterValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class TrackerRequestBuilder {

    private final String url;
    private final List<NamedParameterValue> parameters = new ArrayList<>();

    public TrackerRequestBuilder(final String url) {
        this.url = Objects.requireNonNull(url, "url is null");
    }

    public TrackerRequestBuilder parameter(final NamedParameterValue parameter) {
        parameters.add(parameter);
        return this;
    }

    public TrackerRequestBuilder parameter(final String name, final byte[] value) {
        return parameter(new BinaryNamedParameterValue(name, value));
    }

    public TrackerRequestBuilder parameter(final String name, final Number value) {
        return parameter(new NumberNamedParameterValue(name, value));
    }

    public TrackerRequestBuilder parameter(final String name, final String value) {
        return parameter(new StringNamedParameterValue(name, value));
    }

    public String toQuery() {
        if (parameters.isEmpty()) {
            return url;
        }

        var ret = new StringBuilder();
        ret.append(url);
        ret.append("?");
        ret.append(parameters.stream()
                           .map(parameter -> parameter.name() + "=" + parameter.value())
                           .collect(Collectors.joining("&")));
        return ret.toString();
    }
}
