package com.github.jurisliepins.torrent;

import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.info.MetaInfo;
import com.github.jurisliepins.types.StatusType;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@Builder
public final class TorrentState {
    @NonNull private StatusType status;
    @NonNull private InfoHash infoHash;
    @NonNull private Object peerId;
    @NonNull private Bitfield bitfield;
    @NonNull private List<Object> pieces;
    @NonNull private List<Object> files;
    @NonNull private String name;
    @NonNull private String announce;
    private int pieceLength;
    private long length;
    private long downloaded;
    private long uploaded;
    private long left;
    private double downloadRate;
    private double uploadRate;

    public static TorrentState of(@NonNull final MetaInfo metaInfo) {
        return TorrentState.builder()
                .status(StatusType.Stopped)
                .infoHash(metaInfo.info().hash())
                .peerId(new Object())
                .bitfield(new Bitfield(metaInfo.info().pieces().length))
                .pieces(List.of())
                .files(List.of())
                .name(metaInfo.info().name())
                .announce(metaInfo.announce())
                .pieceLength(metaInfo.info().pieceLength())
                .length(metaInfo.info().length())
                .downloaded(0L)
                .uploaded(0L)
                .left(metaInfo.info().length())
                .downloadRate(0.0)
                .uploadRate(0.0)
                .build();
    }
}
