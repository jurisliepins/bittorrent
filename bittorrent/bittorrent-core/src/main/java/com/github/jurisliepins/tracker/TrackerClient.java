package com.github.jurisliepins.tracker;

import com.github.jurisliepins.CoreException;

import java.io.Closeable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class TrackerClient implements Closeable {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public TrackerResponse announce(final String query) {
        try {
            final HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(query))
                    .GET()
                    .header("User-Agent", "JBitTorrent/0.01")
                    .header("Accept", "*/*")
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
