package com.github.jurisliepins.announcer;

import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.peer.PeerId;
import com.github.jurisliepins.types.State;
import com.github.jurisliepins.types.StatusType;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public final class AnnouncerState implements State {
    @NonNull
    private StatusType status;
    @NonNull
    private InfoHash infoHash;
    @NonNull
    private PeerId selfPeerId;
    @NonNull
    private String announce;
    private int peerCount;
    private int port;
    private int intervalSeconds;
    private long downloaded;
    private long uploaded;
    private long left;
}
