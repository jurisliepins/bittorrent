package com.github.jurisliepins.tracker;

import com.github.jurisliepins.tracker.url.BinaryNamedParameterValue;
import com.github.jurisliepins.tracker.url.NamedParameterValue;
import com.github.jurisliepins.tracker.url.NumberNamedParameterValue;
import com.github.jurisliepins.tracker.url.StringNamedParameterValue;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class TrackerRequestBuilder {

    private final String url;
    private final List<NamedParameterValue> parameters = new ArrayList<>();

    public TrackerRequestBuilder(@NonNull final String url) {
        this.url = url;
    }

    public TrackerRequestBuilder parameter(@NonNull final NamedParameterValue parameter) {
        parameters.add(parameter);
        return this;
    }

    public TrackerRequestBuilder parameter(@NonNull final String name, final byte[] value) {
        if (value != null) {
            return parameter(new BinaryNamedParameterValue(name, value));
        }
        return this;
    }

    public TrackerRequestBuilder parameter(@NonNull final String name, final Number value) {
        if (value != null) {
            return parameter(new NumberNamedParameterValue(name, value));
        }
        return this;
    }

    public TrackerRequestBuilder parameter(@NonNull final String name, final String value) {
        if (value != null) {
            return parameter(new StringNamedParameterValue(name, value));
        }
        return this;
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
