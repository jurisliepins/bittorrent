package com.github.jurisliepins.tracker;

import com.github.jurisliepins.BitTorrentClient;
import com.github.jurisliepins.CoreException;
import lombok.NonNull;

import java.io.Closeable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class TrackerClient implements Closeable {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public TrackerResponse announce(@NonNull final String query) {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(new URI(query))
                    .GET()
                    .header("User-Agent", "%s/%s".formatted(BitTorrentClient.NAME, BitTorrentClient.VERSION))
                    .header("Accept", "*/*")
                    .build();
            var bytes = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofByteArray())
                    .body();
            return TrackerResponseParser.fromBytes(bytes);
        } catch (Exception e) {
            throw new CoreException("Failed to announce", e);
        }
    }

    @Override
    public void close() {
        httpClient.close();
    }
}
