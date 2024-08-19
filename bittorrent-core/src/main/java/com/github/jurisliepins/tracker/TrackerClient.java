package com.github.jurisliepins.tracker;

import com.github.jurisliepins.BitTorrentClient;
import com.github.jurisliepins.CoreException;

import java.io.Closeable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class TrackerClient implements Closeable {

    private static final String USER_AGENT_KEY = "User-Agent";
    private static final String ACCEPT_KEY = "Accept";

    private static final String USER_AGENT_VALUE = "%s/%s".formatted(BitTorrentClient.NAME, BitTorrentClient.VERSION);
    private static final String ACCEPT_VALUE = "*/*";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public TrackerResponse announce(final String query) {
        try {
            final HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(query))
                    .GET()
                    .header(USER_AGENT_KEY, USER_AGENT_VALUE)
                    .header(ACCEPT_KEY, ACCEPT_VALUE)
                    .build();
            final byte[] bytes = httpClient
                    .send(httpRequest, HttpResponse.BodyHandlers.ofByteArray())
                    .body();
            return TrackerResponse.fromBytes(bytes);
        } catch (Exception e) {
            throw new CoreException("Failed to announce", e);
        }
    }

    @Override
    public void close() {
        httpClient.close();
    }
}
