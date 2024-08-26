package com.github.jurisliepins.torrent;

import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.types.StatusType;

import java.util.List;
import java.util.Objects;

public final class TorrentState {
    private StatusType status;
    private InfoHash infoHash;
    private Object peerId;
    private Bitfield bitfield;
    private int pieceLength;
    private List<Object> pieces;
    private List<Object> files;
    private String name;
    private long length;
    private long downloaded;
    private long uploaded;
    private long left;
    private String announce;
    private double downloadRate;
    private double uploadRate;

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(final StatusType status) {
        this.status = Objects.requireNonNull(status, "status is null");
    }

    public InfoHash getInfoHash() {
        return infoHash;
    }

    public void setInfoHash(final InfoHash infoHash) {
        this.infoHash = Objects.requireNonNull(infoHash, "infoHash is null");
    }

    public Object getPeerId() {
        return peerId;
    }

    public void setPeerId(final Object peerId) {
        this.peerId = Objects.requireNonNull(peerId, "peerId is null");
    }

    public Bitfield getBitfield() {
        return bitfield;
    }

    public void setBitfield(final Bitfield bitfield) {
        this.bitfield = Objects.requireNonNull(bitfield, "bitfield is null");
    }

    public int getPieceLength() {
        return pieceLength;
    }

    public void setPieceLength(final int pieceLength) {
        this.pieceLength = pieceLength;
    }

    public List<Object> getPieces() {
        return pieces;
    }

    public void setPieces(final List<Object> pieces) {
        this.pieces = Objects.requireNonNull(pieces, "pieces is null");
    }

    public List<Object> getFiles() {
        return files;
    }

    public void setFiles(final List<Object> files) {
        this.files = Objects.requireNonNull(files, "files is null");
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = Objects.requireNonNull(name, "name is null");
    }

    public long getLength() {
        return length;
    }

    public void setLength(final long length) {
        this.length = length;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(final long downloaded) {
        this.downloaded = downloaded;
    }

    public long getUploaded() {
        return uploaded;
    }

    public void setUploaded(final long uploaded) {
        this.uploaded = uploaded;
    }

    public long getLeft() {
        return left;
    }

    public void setLeft(final long left) {
        this.left = left;
    }

    public String getAnnounce() {
        return announce;
    }

    public void setAnnounce(final String announce) {
        this.announce = Objects.requireNonNull(announce, "announce is null");
    }

    public double getDownloadRate() {
        return downloadRate;
    }

    public void setDownloadRate(final double downloadRate) {
        this.downloadRate = downloadRate;
    }

    public double getUploadRate() {
        return uploadRate;
    }

    public void setUploadRate(final double uploadRate) {
        this.uploadRate = uploadRate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private StatusType withStatus;
        private InfoHash withInfoHash;
        private Object withPeerId;
        private Bitfield withBitfield;
        private int withPieceLength;
        private List<Object> withPieces;
        private List<Object> withFiles;
        private String withName;
        private long withLength;
        private long withDownloaded;
        private long withUploaded;
        private long withLeft;
        private String withAnnounce;
        private double withDownloadRate;
        private double withUploadRate;

        public Builder status(final StatusType status) {
            this.withStatus = status;
            return this;
        }

        public Builder infoHash(final InfoHash infoHash) {
            this.withInfoHash = infoHash;
            return this;
        }

        public Builder peerId(final Object peerId) {
            this.withPeerId = peerId;
            return this;
        }

        public Builder bitfield(final Bitfield bitfield) {
            this.withBitfield = bitfield;
            return this;
        }

        public Builder pieceLength(final int pieceLength) {
            this.withPieceLength = pieceLength;
            return this;
        }

        public Builder pieces(final List<Object> pieces) {
            this.withPieces = pieces;
            return this;
        }

        public Builder files(final List<Object> files) {
            this.withFiles = files;
            return this;
        }

        public Builder name(final String name) {
            this.withName = name;
            return this;
        }

        public Builder length(final long length) {
            this.withLength = length;
            return this;
        }

        public Builder downloaded(final long downloaded) {
            this.withDownloaded = downloaded;
            return this;
        }

        public Builder uploaded(final long uploaded) {
            this.withUploaded = uploaded;
            return this;
        }

        public Builder left(final long left) {
            this.withLeft = left;
            return this;
        }

        public Builder announce(final String announce) {
            this.withAnnounce = announce;
            return this;
        }

        public Builder downloadRate(final double downloadRate) {
            this.withDownloadRate = downloadRate;
            return this;
        }

        public Builder uploadRate(final double uploadRate) {
            this.withUploadRate = uploadRate;
            return this;
        }

        public TorrentState build() {
            final TorrentState state = new TorrentState();
            state.setStatus(withStatus);
            state.setInfoHash(withInfoHash);
            state.setPeerId(withPeerId);
            state.setBitfield(withBitfield);
            state.setPieceLength(withPieceLength);
            state.setPieces(withPieces);
            state.setFiles(withFiles);
            state.setName(withName);
            state.setLength(withLength);
            state.setDownloaded(withDownloaded);
            state.setUploaded(withUploaded);
            state.setLeft(withLeft);
            state.setAnnounce(withAnnounce);
            state.setDownloadRate(withDownloadRate);
            state.setUploadRate(withUploadRate);
            return state;
        }
    }
}
