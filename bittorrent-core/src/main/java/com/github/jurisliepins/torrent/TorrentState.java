package com.github.jurisliepins.torrent;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.common.StatusType;
import com.github.jurisliepins.info.Hash;
import com.github.jurisliepins.peer.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.OffsetDateTime;

@Data
@Builder
public final class TorrentState {
    @NonNull
    private StatusType status;

    @NonNull
    private Hash infoHash;

    @NonNull
    private Id selfId;

    @NonNull
    private Bitfield bitfield;

    @NonNull
    private String name;

    @NonNull
    private Integer pieceLength;

    @NonNull
    private Long length;

    @NonNull
    private String announce;

    private String[][] announceList;

    private OffsetDateTime creationDate;

    private String comment;

    private String createdBy;

    private String encoding;

    @NonNull
    private Long downloaded;

    @NonNull
    private Long uploaded;

    @NonNull
    private Long left;

    @NonNull
    private ActorRef announcerRef;

    @NonNull
    private ActorRef notifiedRef;
}
