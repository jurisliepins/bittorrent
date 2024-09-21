package com.github.jurisliepins.client.message;

import com.github.jurisliepins.bitfield.ImmutableBitfield;
import com.github.jurisliepins.common.StatusType;
import com.github.jurisliepins.info.Hash;
import com.github.jurisliepins.peer.Id;
import lombok.Builder;
import lombok.NonNull;

import java.time.OffsetDateTime;

public sealed interface ClientResponse permits
        ClientResponse.Get,
        ClientResponse.Failure {

    @Builder
    record Torrent(
            @NonNull StatusType status,
            @NonNull Hash infoHash,
            @NonNull Id selfId,
            @NonNull ImmutableBitfield bitfield,
            @NonNull String name,
            @NonNull Integer pieceLength,
            @NonNull Long length,
            @NonNull String announce,
            String[][] announceList,
            OffsetDateTime creationDate,
            String comment,
            String createdBy,
            String encoding,
            @NonNull Long downloaded,
            @NonNull Long uploaded,
            @NonNull Long left
    ) { }

    record Get(
            @NonNull Torrent torrent
    ) implements ClientResponse { }

    record Failure(
            @NonNull Hash infoHash,
            @NonNull String message
    ) implements ClientResponse { }
}
