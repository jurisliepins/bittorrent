package com.github.jurisliepins.announcer;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.common.StatusType;
import com.github.jurisliepins.info.Hash;
import com.github.jurisliepins.peer.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public final class AnnouncerState {
    @NonNull
    private StatusType status;

    @NonNull
    private Hash infoHash;

    @NonNull
    private Id selfId;

    @NonNull
    private String announce;

    private String[][] announceList;

    @NonNull
    private Integer peerCount;

    @NonNull
    private Integer port;

    @NonNull
    private Integer intervalSeconds;

    @NonNull
    private Long downloaded;

    @NonNull
    private Long uploaded;

    @NonNull
    private Long left;

    @NonNull
    private ActorRef notifiedRef;
}
