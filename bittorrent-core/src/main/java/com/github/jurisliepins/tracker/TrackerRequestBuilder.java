package com.github.jurisliepins.tracker;

import com.github.jurisliepins.info.Hash;
import com.github.jurisliepins.peer.Id;
import com.github.jurisliepins.tracker.url.BinaryNamedParameterValue;
import com.github.jurisliepins.tracker.url.NamedParameterValue;
import com.github.jurisliepins.tracker.url.NumberNamedParameterValue;
import com.github.jurisliepins.tracker.url.StringNamedParameterValue;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

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

    public TrackerRequestBuilder infoHash(final Hash infoHash) {
        return parameter(TrackerRequest.INFO_HASH, requireNonNull(infoHash).toBytes());
    }

    public TrackerRequestBuilder peerId(final Id peerId) {
        return parameter(TrackerRequest.PEER_ID, requireNonNull(peerId).toString());
    }

    public TrackerRequestBuilder port(final int port) {
        return parameter(TrackerRequest.PORT, port);
    }

    public TrackerRequestBuilder uploaded(final long uploaded) {
        return parameter(TrackerRequest.UPLOADED, uploaded);
    }

    public TrackerRequestBuilder downloaded(final long downloaded) {
        return parameter(TrackerRequest.DOWNLOADED, downloaded);
    }

    public TrackerRequestBuilder left(final long left) {
        return parameter(TrackerRequest.LEFT, left);
    }

    public TrackerRequestBuilder compact(final int compact) {
        return parameter(TrackerRequest.COMPACT, compact);
    }

    public TrackerRequestBuilder event(final Optional<TrackerEventType> eventOpt) {
        return parameter(TrackerRequest.EVENT, eventOpt.map(TrackerEventType::toString).orElse(null));
    }

    public TrackerRequestBuilder noPeerId(final int noPeerId) {
        return parameter(TrackerRequest.NO_PEER_ID, noPeerId);
    }

    public TrackerRequestBuilder ip(final String ip) {
        return parameter(TrackerRequest.IP, requireNonNull(ip));
    }

    public TrackerRequestBuilder numWant(final int numWant) {
        return parameter(TrackerRequest.NUM_WANT, numWant);
    }

    public TrackerRequestBuilder key(final String key) {
        return parameter(TrackerRequest.KEY, requireNonNull(key));
    }

    public TrackerRequestBuilder trackerId(final String trackerId) {
        return parameter(TrackerRequest.TRACKER_ID, requireNonNull(trackerId));
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
