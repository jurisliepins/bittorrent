package com.github.jurisliepins;

import com.github.jurisliepins.client.ClientMailboxReceiver;
import com.github.jurisliepins.client.message.ClientCommand;
import com.github.jurisliepins.client.message.ClientCommandResult;
import com.github.jurisliepins.client.message.ClientRequest;
import com.github.jurisliepins.client.message.ClientResponse;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.peer.PeerId;
import com.github.jurisliepins.types.StatusType;
import lombok.NonNull;

import java.util.concurrent.TimeUnit;

public final class BitTorrentClient {
    private static final long TIMEOUT_MS = 5_000;

    private final ActorSystem system = new ActorSystem();

    private final ActorRef clientRef;

    public BitTorrentClient() {
        clientRef = system.spawn(new ClientMailboxReceiver(Context.defaultContext(), ClientState.builder()
                .status(StatusType.Started)
                .selfPeerId(PeerId.self())
                .torrents(new ClientState.Torrents())
                .settings(ClientState.Settings.builder()
                                  .peerCount(Context.DEFAULT_PEER_COUNT)
                                  .port(Context.DEFAULT_PORT)
                                  .intervalSeconds(Context.DEFAULT_INTERVAL_SECONDS)
                                  .build())
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
        system.shutdown();
    }

    private <T, U> U postWithReply(@NonNull final T message) {
        return clientRef.postWithReply(message, TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }
}
