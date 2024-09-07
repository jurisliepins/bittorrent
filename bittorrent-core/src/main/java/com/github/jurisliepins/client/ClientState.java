package com.github.jurisliepins.client;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.types.StatusType;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public final class ClientState {

    private final Map<InfoHash, Torrent> torrents = new HashMap<>();

    public Torrent get(@NonNull final InfoHash infoHash) {
        return torrents.get(infoHash);
    }

    public void add(@NonNull final Torrent torrent) {
        torrents.put(torrent.getInfoHash(), torrent);
    }

    public Torrent remove(@NonNull final InfoHash infoHash) {
        return torrents.remove(infoHash);
    }

    @Data
    @Builder
    public static final class Torrent {
        @NonNull private ActorRef ref;
        @NonNull private StatusType status;
        @NonNull private InfoHash infoHash;
        @NonNull private Object peerId;
        @NonNull private Bitfield bitfield;
        @NonNull private String name;
        private int pieceLength;
        private long length;
        private long downloaded;
        private long uploaded;
        private long left;
        private double downloadRate;
        private double uploadRate;
    }
}
