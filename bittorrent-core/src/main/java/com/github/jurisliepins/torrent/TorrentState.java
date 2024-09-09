package com.github.jurisliepins.torrent;

import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.peer.PeerId;
import com.github.jurisliepins.types.StatusType;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@Builder
public final class TorrentState {
    @NonNull
    private StatusType status;
    @NonNull
    private InfoHash infoHash;
    @NonNull
    private PeerId selfPeerId;
    @NonNull
    private Bitfield bitfield;
    @NonNull
    private List<Object> pieces;
    @NonNull
    private List<Object> files;
    @NonNull
    private String name;
    @NonNull
    private String announce;
    private int pieceLength;
    private long length;
    private long downloaded;
    private long uploaded;
    private long left;
    private double downloadRate;
    private double uploadRate;
}
