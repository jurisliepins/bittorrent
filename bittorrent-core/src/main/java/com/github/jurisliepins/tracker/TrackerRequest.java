package com.github.jurisliepins.tracker;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class TrackerRequest {
    public static final String INFO_HASH = "info_hash";
    public static final String PEER_ID = "peer_id";
    public static final String PORT = "port";
    public static final String UPLOADED = "uploaded";
    public static final String DOWNLOADED = "downloaded";
    public static final String LEFT = "left";
    public static final String COMPACT = "compact";
    public static final String NO_PEER_ID = "no_peer_id";
    public static final String EVENT = "event";
    public static final String IP = "ip";
    public static final String NUM_WANT = "numwant";
    public static final String KEY = "key";
    public static final String TRACKER_ID = "trackerid";

    public static TrackerRequestBuilder builder(final String url) {
        return new TrackerRequestBuilder(url);
    }
}
