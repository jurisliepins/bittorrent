package com.github.jurisliepins.torrent.state;

import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.info.MetaInfo;

import java.util.List;

public final class TorrentState {
    private Status status;
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

    public TorrentState(final MetaInfo metaInfo) {
        this.status = Status.STOPPED;
        this.infoHash = metaInfo.info().hash();
        this.peerId = new Object();
        this.bitfield = new Bitfield(metaInfo.info().pieces().length);
        this.pieceLength = metaInfo.info().pieceLength();
        this.pieces = List.of();
        this.files = List.of();
        this.name = metaInfo.info().name();
        this.length = metaInfo.info().length();
        this.downloaded = 0L;
        this.uploaded = 0L;
        this.left = metaInfo.info().length();
        this.announce = metaInfo.announce();
        this.downloadRate = 0.0;
        this.uploadRate = 0.0;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public InfoHash getInfoHash() {
        return infoHash;
    }

    public void setInfoHash(final InfoHash infoHash) {
        this.infoHash = infoHash;
    }

    public Object getPeerId() {
        return peerId;
    }

    public void setPeerId(final Object peerId) {
        this.peerId = peerId;
    }

    public Bitfield getBitfield() {
        return bitfield;
    }

    public void setBitfield(final Bitfield bitfield) {
        this.bitfield = bitfield;
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
        this.pieces = pieces;
    }

    public List<Object> getFiles() {
        return files;
    }

    public void setFiles(final List<Object> files) {
        this.files = files;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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
        this.announce = announce;
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

    public enum Status {
        STARTED,
        RUNNING,
        STOPPED,
        ERRORED
    }
}
