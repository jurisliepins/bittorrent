package com.github.jurisliepins.torrent;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.common.StatusType;
import com.github.jurisliepins.info.Hash;
import com.github.jurisliepins.peer.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

import java.time.OffsetDateTime;

@Data
@Builder
public final class TorrentState {
    @NonNull
    private StatusType status;

    @NonNull
    @Setter(AccessLevel.NONE)
    private Hash infoHash;

    @NonNull
    @Setter(AccessLevel.NONE)
    private Id selfId;

    @NonNull
    private Bitfield bitfield;

    @NonNull
    @Setter(AccessLevel.NONE)
    private String name;

    @NonNull
    @Setter(AccessLevel.NONE)
    private Integer pieceLength;

    @NonNull
    @Setter(AccessLevel.NONE)
    private Long length;

    @NonNull
    @Setter(AccessLevel.NONE)
    private String announce;

    @Setter(AccessLevel.NONE)
    private String[][] announceList;

    @Setter(AccessLevel.NONE)
    private OffsetDateTime creationDate;

    @Setter(AccessLevel.NONE)
    private String comment;

    @Setter(AccessLevel.NONE)
    private String createdBy;

    @Setter(AccessLevel.NONE)
    private String encoding;

    @NonNull
    private Long downloaded;

    @NonNull
    private Long uploaded;

    @NonNull
    private Long left;

    @NonNull
    @Setter(AccessLevel.NONE)
    private ActorRef announcerRef;

    @NonNull
    @Setter(AccessLevel.NONE)
    private ActorRef notifiedRef;
}
