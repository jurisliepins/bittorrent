package com.github.jurisliepins.client;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.types.StatusType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ClientState {

    private final Map<InfoHash, Torrent> torrents = new HashMap<>();

    public Torrent get(final InfoHash infoHash) {
        return torrents.get(infoHash);
    }

    public void add(final Torrent torrent) {
        torrents.put(torrent.getInfoHash(), torrent);
    }

    public Torrent remove(final InfoHash infoHash) {
        return torrents.remove(infoHash);
    }

    public static final class Torrent {
        private ActorRef ref;
        private StatusType status;
        private InfoHash infoHash;
        private Object peerId;
        private Bitfield bitfield;
        private int pieceLength;
        private String name;
        private long length;
        private long downloaded;
        private long uploaded;
        private long left;
        private double downloadRate;
        private double uploadRate;

        public ActorRef getRef() {
            return ref;
        }

        public void setRef(final ActorRef ref) {
            this.ref = Objects.requireNonNull(ref, "ref is null");
        }

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
            private ActorRef withRef;
            private StatusType withStatus;
            private InfoHash withInfoHash;
            private Object withPeerId;
            private Bitfield withBitfield;
            private int withPieceLength;
            private String withName;
            private long withLength;
            private long withDownloaded;
            private long withUploaded;
            private long withLeft;
            private double withDownloadRate;
            private double withUploadRate;

            public Builder ref(final ActorRef ref) {
                this.withRef = ref;
                return this;
            }

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

            public Builder downloadRate(final double downloadRate) {
                this.withDownloadRate = downloadRate;
                return this;
            }

            public Builder uploadRate(final double uploadRate) {
                this.withUploadRate = uploadRate;
                return this;
            }

            public Torrent build() {
                final Torrent torrent = new Torrent();
                torrent.setRef(withRef);
                torrent.setStatus(withStatus);
                torrent.setInfoHash(withInfoHash);
                torrent.setPeerId(withPeerId);
                torrent.setBitfield(withBitfield);
                torrent.setPieceLength(withPieceLength);
                torrent.setName(withName);
                torrent.setLength(withLength);
                torrent.setDownloaded(withDownloaded);
                torrent.setUploaded(withUploaded);
                torrent.setLeft(withLeft);
                torrent.setDownloadRate(withDownloadRate);
                torrent.setUploadRate(withUploadRate);
                return torrent;
            }
        }
    }
}
