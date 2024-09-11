package com.github.jurisliepins.client.message;

import com.github.jurisliepins.bitfield.ImmutableBitfield;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.peer.PeerId;
import com.github.jurisliepins.types.StatusType;
import lombok.Builder;
import lombok.NonNull;

public sealed interface ClientResponse permits
        ClientResponse.Get,
        ClientResponse.Failure {

    @Builder
    record Torrent(
            @NonNull StatusType status,
            @NonNull InfoHash infoHash,
            @NonNull PeerId selfPeerId,
            @NonNull ImmutableBitfield bitfield,
            @NonNull String name,
            long length,
            long downloaded,
            long uploaded,
            long left,
            double downloadRate,
            double uploadRate
    ) {
        public static Torrent of(final ClientState.Torrent torrent) {
            return Torrent.builder()
                    .status(torrent.getStatus())
                    .infoHash(torrent.getInfoHash())
                    .selfPeerId(torrent.getSelfPeerId())
                    .bitfield(torrent.getBitfield())
                    .name(torrent.getName())
                    .length(torrent.getLength())
                    .downloaded(torrent.getDownloaded())
                    .uploaded(torrent.getUploaded())
                    .left(torrent.getLeft())
                    .downloadRate(torrent.getDownloadRate())
                    .uploadRate(torrent.getUploadRate())
                    .build();
        }
    }

    record Get(@NonNull Torrent torrent) implements ClientResponse { }

    record Failure(
            @NonNull InfoHash infoHash,
            @NonNull String resultMessage
    ) implements ClientResponse { }
}
