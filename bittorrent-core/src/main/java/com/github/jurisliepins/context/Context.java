package com.github.jurisliepins.context;

import com.github.jurisliepins.tracker.TrackerClient;
import com.github.jurisliepins.tracker.TrackerClientImpl;
import lombok.NonNull;

public record Context(@NonNull TrackerClient trackerClient) {
    public static final String CLIENT_ID = "ZZ";
    public static final String CLIENT_NAME = "BitTorrent";
    public static final String CLIENT_VERSION = "0001";

    public static final int DEFAULT_PEER_COUNT = 30;
    public static final int DEFAULT_PORT = 6881;
    public static final int DEFAULT_INTERVAL_SECONDS = 60;

    public static Context defaultConfig() {
        return new Context(new TrackerClientImpl());
    }
}
