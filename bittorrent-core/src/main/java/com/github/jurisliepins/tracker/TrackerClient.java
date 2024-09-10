package com.github.jurisliepins.tracker;

public interface TrackerClient {

    TrackerResponse announce(String query);

}
