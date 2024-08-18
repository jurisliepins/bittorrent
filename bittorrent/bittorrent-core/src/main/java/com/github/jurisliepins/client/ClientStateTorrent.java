package com.github.jurisliepins.client;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.types.StatusType;

import java.util.Objects;

public final class ClientStateTorrent {
    private ActorRef ref;
    private StatusType status;
    private InfoHash infoHash;
    private Object peerId;
    private Bitfield bitfield;
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
}
