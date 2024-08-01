package com.github.jurisliepins;

import com.github.jurisliepins.client.Client;
import com.github.jurisliepins.client.ClientCommand;
import com.github.jurisliepins.client.ClientCommandResult;
import com.github.jurisliepins.client.ClientRequest;
import com.github.jurisliepins.client.ClientResponse;
import com.github.jurisliepins.client.state.DefaultClientState;
import com.github.jurisliepins.info.InfoHash;

import java.util.concurrent.TimeUnit;

public final class BitTorrentClient {

    private static final long TIMEOUT_MS = 5_000;

    private final ActorSystem actorSystem = new ActorSystem();

    private final ActorRef clientRef;

    public BitTorrentClient() {
        clientRef = actorSystem.spawn(new Client(new DefaultClientState()));
    }

    public ClientResponse get(final InfoHash infoHash) {
        return postWithReply(new ClientRequest.Get(infoHash));
    }

    public ClientCommandResult add(final byte[] metaInfo) {
        return postWithReply(new ClientCommand.Add(metaInfo));
    }

    public ClientCommandResult remove(final InfoHash infoHash) {
        return postWithReply(new ClientCommand.Remove(infoHash));
    }

    public ClientCommandResult start(final InfoHash infoHash) {
        return postWithReply(new ClientCommand.Start(infoHash));
    }

    public ClientCommandResult stop(final InfoHash infoHash) {
        return postWithReply(new ClientCommand.Stop(infoHash));
    }

    public void shutdown() {
        actorSystem.shutdown();
    }

    private <T, U> U postWithReply(final T message) {
        return clientRef.postWithReply(message, TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

}
