package com.github.jurisliepins.client;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.info.MetaInfo;
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

        public static Torrent of(final ActorRef ref, final MetaInfo metaInfo) {
            return ClientState.Torrent.builder()
                    .ref(ref)
                    .status(StatusType.Stopped)
                    .infoHash(metaInfo.info().hash())
                    .peerId(new Object())
                    .bitfield(new Bitfield(metaInfo.info().pieces().length))
                    .pieceLength(metaInfo.info().pieceLength())
                    .name(metaInfo.info().name())
                    .length(metaInfo.info().length())
                    .downloaded(0L)
                    .uploaded(0L)
                    .left(metaInfo.info().length())
                    .downloadRate(0.0)
                    .uploadRate(0.0)
                    .build();
        }
    }
}
