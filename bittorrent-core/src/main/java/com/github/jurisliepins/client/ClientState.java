package com.github.jurisliepins.client;

import com.github.jurisliepins.Actor;
import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.bitfield.ImmutableBitfield;
import com.github.jurisliepins.common.StatusType;
import com.github.jurisliepins.info.Hash;
import com.github.jurisliepins.peer.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public final class ClientState {

    @NonNull
    @Builder.Default
    private Torrents torrents = Torrents.emptyTorrents();

    @NonNull
    @Builder.Default
    private Settings settings = Settings.defaultSettings();

    @Data
    @Builder
    public static final class Torrent {
        @NonNull
        @Builder.Default
        private ActorRef ref = Actor.DeadLetter.INSTANCE;

        @NonNull
        @Builder.Default
        private StatusType status = StatusType.Stopped;

        @NonNull
        @Builder.Default
        private Hash infoHash = Hash.BLANK;

        @NonNull
        @Builder.Default
        private Id selfId = Id.BLANK;

        @NonNull
        @Builder.Default
        private ImmutableBitfield bitfield = Bitfield.BLANK;

        @NonNull
        @Builder.Default
        private String name = "";

        @NonNull
        @Builder.Default
        private Integer pieceLength = 0;

        @NonNull
        @Builder.Default
        private Long length = 0L;

        @NonNull
        @Builder.Default
        private String announce = "";

        @Builder.Default
        private String[][] announceList = new String[][]{};

        @Builder.Default
        private OffsetDateTime creationDate = OffsetDateTime.MIN;

        @Builder.Default
        private String comment = "";

        @Builder.Default
        private String createdBy = "";

        @Builder.Default
        private String encoding = "";

        @NonNull
        @Builder.Default
        private Long downloaded = 0L;

        @NonNull
        @Builder.Default
        private Long uploaded = 0L;

        @NonNull
        @Builder.Default
        private Long left = 0L;
    }

    public static final class Torrents {
        private final Map<Hash, Torrent> torrents = new HashMap<>();

        public Torrent get(final Hash infoHash) {
            return torrents.get(infoHash);
        }

        public Torrent add(final Torrent torrent) {
            return torrents.put(torrent.getInfoHash(), torrent);
        }

        public Torrent remove(final Hash infoHash) {
            return torrents.remove(infoHash);
        }

        public static Torrents emptyTorrents() {
            return new Torrents();
        }
    }

    public record Settings(
            int peerCount,
            int port,
            int intervalSeconds
    ) {
        public static final int DEFAULT_PEER_COUNT = 30;
        public static final int DEFAULT_PORT = 6881;
        public static final int DEFAULT_INTERVAL_SECONDS = 60;

        public static Settings defaultSettings() {
            return new Settings(
                    DEFAULT_PEER_COUNT,
                    DEFAULT_PORT,
                    DEFAULT_INTERVAL_SECONDS
            );
        }
    }

    public static ClientState emptyState() {
        return ClientState.builder().build();
    }
}
