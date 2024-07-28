package com.github.jurisliepins.client;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.info.MetaInfo;

import java.util.Objects;

public final class Torrent {
    private ActorRef ref;
    private Status status;
    private InfoHash infoHash;
    private Object peerId;
    private Object bitfield;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
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

    public Object getBitfield() {
        return bitfield;
    }

    public void setBitfield(final Object bitfield) {
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

//    public static Torrent fromMetaInfo(final ActorRef ref, final MetaInfo metaInfo) {
//        final Torrent torrent = new Torrent();
//        torrent.setRef(ref);
//        torrent.setStatus(Status.STOPPED);
//        torrent.setInfoHash(metaInfo.info().hash());
//        torrent.setPeerId(new Object());
//        torrent.setBitfield(new Object());
//        torrent.setName(metaInfo.info().name());
//        torrent.setLength(metaInfo.info().length());
//        torrent.
//        return torrent;
//    }

    public enum Status {
        STARTED,
        STOPPED,
        ERRORED
    }
}
