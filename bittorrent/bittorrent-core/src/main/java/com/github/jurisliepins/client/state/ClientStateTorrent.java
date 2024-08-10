package com.github.jurisliepins.client.state;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.info.MetaInfo;

import java.util.Objects;

public final class ClientStateTorrent implements ImmutableClientStateTorrent {
    private ActorRef ref;
    private Status status;
    private InfoHash infoHash;
//    private Object peerId;
    private Bitfield bitfield;
    private String name;
    private long length;
    private long downloaded;
    private long uploaded;
    private long left;
    private double downloadRate;
    private double uploadRate;

    public ClientStateTorrent(final ActorRef ref, final MetaInfo metaInfo) {
        this.ref = Objects.requireNonNull(ref, "ref is null");
        this.status = Status.Stopped;
        this.infoHash = metaInfo.info().hash();
//        this.peerId = new Object();
        this.bitfield = new Bitfield(metaInfo.info().pieces().length);
        this.name = metaInfo.info().name();
        this.length = metaInfo.info().length();
        this.downloaded = 0L;
        this.uploaded = 0L;
        this.left = metaInfo.info().length();
        this.downloadRate = 0.0;
        this.uploadRate = 0.0;
    }

    public ActorRef getRef() {
        return ref;
    }

    public void setRef(final ActorRef ref) {
        this.ref = Objects.requireNonNull(ref, "ref is null");
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = Objects.requireNonNull(status, "status is null");
    }

    @Override
    public InfoHash getInfoHash() {
        return infoHash;
    }

    public void setInfoHash(final InfoHash infoHash) {
        this.infoHash = Objects.requireNonNull(infoHash, "infoHash is null");
    }

//    @Override
//    public Object getPeerId() {
//        return peerId;
//    }
//
//    public void setPeerId(final Object peerId) {
//        this.peerId = Objects.requireNonNull(peerId, "peerId is null");
//    }

    @Override
    public Bitfield getBitfield() {
        return bitfield;
    }

    public void setBitfield(final Bitfield bitfield) {
        this.bitfield = Objects.requireNonNull(bitfield, "bitfield is null");
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = Objects.requireNonNull(name, "name is null");
    }

    @Override
    public long getLength() {
        return length;
    }

    public void setLength(final long length) {
        this.length = length;
    }

    @Override
    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(final long downloaded) {
        this.downloaded = downloaded;
    }

    @Override
    public long getUploaded() {
        return uploaded;
    }

    public void setUploaded(final long uploaded) {
        this.uploaded = uploaded;
    }

    @Override
    public long getLeft() {
        return left;
    }

    public void setLeft(final long left) {
        this.left = left;
    }

    @Override
    public double getDownloadRate() {
        return downloadRate;
    }

    public void setDownloadRate(final double downloadRate) {
        this.downloadRate = downloadRate;
    }

    @Override
    public double getUploadRate() {
        return uploadRate;
    }

    public void setUploadRate(final double uploadRate) {
        this.uploadRate = uploadRate;
    }

    public enum Status {
        Started,
        Stopped,
        Errored
    }
}
