package com.github.jurisliepins;

import com.github.jurisliepins.client.ClientMailboxReceiver;
import com.github.jurisliepins.client.message.ClientCommand;
import com.github.jurisliepins.client.message.ClientCommandResult;
import com.github.jurisliepins.client.message.ClientRequest;
import com.github.jurisliepins.client.message.ClientResponse;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.peer.PeerId;
import lombok.NonNull;

import java.util.concurrent.TimeUnit;

public final class BitTorrentClient {
    public static final String ID = "ZZ";
    public static final String NAME = "BitTorrent";
    public static final String VERSION = "0001";

    private static final long TIMEOUT_MS = 5_000;

    private final ActorSystem actorSystem = new ActorSystem();

    private final ActorRef clientRef;

    public BitTorrentClient() {
        clientRef = actorSystem.spawn(new ClientMailboxReceiver(
                ClientState.builder()
                        .selfPeerId(PeerId.createSelfPeerId())
                        .torrents(new ClientState.Torrents())
                        .peerCount(30)
                        .port(6881)
                        .build()
        ));
    }

    public ClientResponse get(@NonNull final InfoHash infoHash) {
        return postWithReply(new ClientRequest.Get(infoHash));
    }

    public ClientCommandResult add(final byte @NonNull [] metaInfo) {
        return postWithReply(new ClientCommand.Add(metaInfo));
    }

    public ClientCommandResult remove(@NonNull final InfoHash infoHash) {
        return postWithReply(new ClientCommand.Remove(infoHash));
    }

    public ClientCommandResult start(@NonNull final InfoHash infoHash) {
        return postWithReply(new ClientCommand.Start(infoHash));
    }

    public ClientCommandResult stop(@NonNull final InfoHash infoHash) {
        return postWithReply(new ClientCommand.Stop(infoHash));
    }

    public void shutdown() {
        actorSystem.shutdown();
    }

    private <T, U> U postWithReply(@NonNull final T message) {
        return clientRef.postWithReply(message, TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

}
