package com.github.jurisliepins.services;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.client.ClientCommand;
import com.github.jurisliepins.client.ClientCommandResult;
import com.github.jurisliepins.client.ClientRequest;
import com.github.jurisliepins.client.ClientResponse;
import com.github.jurisliepins.info.InfoHash;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.concurrent.TimeUnit;

@ApplicationScoped
public final class TorrentService {

    private static final long TIMEOUT_MS = 5_000;

    @Inject
    ActorRef clientRef;

    public ClientResponse get(final InfoHash infoHash) {
        return clientRef.postWithReply(new ClientRequest.Get(infoHash), TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    public ClientCommandResult add(final Object torrent) {
        return clientRef.postWithReply(new ClientCommand.Add(null), TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    public ClientCommandResult remove(final InfoHash infoHash) {
        return clientRef.postWithReply(new ClientCommand.Remove(infoHash), TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    public ClientCommandResult start(final InfoHash infoHash) {
        return clientRef.postWithReply(new ClientCommand.Start(infoHash), TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    public ClientCommandResult stop(final InfoHash infoHash) {
        return clientRef.postWithReply(new ClientCommand.Stop(infoHash), TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }
}
