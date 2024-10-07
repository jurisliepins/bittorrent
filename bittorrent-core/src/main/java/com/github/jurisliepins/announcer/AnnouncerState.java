package com.github.jurisliepins.announcer;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.common.StatusType;
import com.github.jurisliepins.info.Hash;
import com.github.jurisliepins.peer.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

@Data
@Builder
public final class AnnouncerState {
    @NonNull
    private StatusType status;

    @NonNull
    @Setter(AccessLevel.NONE)
    private Hash infoHash;

    @NonNull
    @Setter(AccessLevel.NONE)
    private Id selfId;

    @NonNull
    @Setter(AccessLevel.NONE)
    private String announce;

    @Setter(AccessLevel.NONE)
    private String[][] announceList;

    @NonNull
    @Setter(AccessLevel.NONE)
    private Integer peerCount;

    @NonNull
    @Setter(AccessLevel.NONE)
    private Integer port;

    @NonNull
    private Long downloaded;

    @NonNull
    private Long uploaded;

    @NonNull
    private Long left;

    @NonNull
    @Setter(AccessLevel.NONE)
    private ActorRef notifiedRef;
}
