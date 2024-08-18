package com.github.jurisliepins.torrent;

import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.types.StatusType;

import java.util.List;

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
}
