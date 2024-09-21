package com.github.jurisliepins;

import com.github.jurisliepins.client.ClientMailboxReceiver;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.client.message.ClientCommand;
import com.github.jurisliepins.client.message.ClientCommandResult;
import com.github.jurisliepins.client.message.ClientRequest;
import com.github.jurisliepins.client.message.ClientResponse;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.info.Hash;
import lombok.NonNull;

import java.util.concurrent.TimeUnit;

public final class BitTorrentClient {
    public static final String CLIENT_ID = "BT";
    public static final String CLIENT_NAME = "BitTorrent";
    public static final String CLIENT_VERSION = "0001";

    private static final long TIMEOUT_MS = 5_000;

    private final ActorSystem system = new ActorSystem();

    private final ActorRef clientRef;

    public BitTorrentClient() {
        this(Context.defaultContext());
    }

    public BitTorrentClient(final Context context) {
        clientRef = system.spawn(new ClientMailboxReceiver(context, ClientState.emptyState()));
    }

    public ClientResponse get(@NonNull final Hash infoHash) {
        return postWithReply(new ClientRequest.Get(infoHash));
    }

    public ClientCommandResult add(final byte @NonNull [] metaInfo) {
        return postWithReply(new ClientCommand.Add(metaInfo));
    }

    public ClientCommandResult remove(@NonNull final Hash infoHash) {
        return postWithReply(new ClientCommand.Remove(infoHash));
    }

    public ClientCommandResult start(@NonNull final Hash infoHash) {
        return postWithReply(new ClientCommand.Start(infoHash));
    }

    public ClientCommandResult stop(@NonNull final Hash infoHash) {
        return postWithReply(new ClientCommand.Stop(infoHash));
    }

    public void shutdown() {
        system.shutdown();
    }

    private <T, U> U postWithReply(@NonNull final T message) {
        return clientRef.postWithReply(message, TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }
}
