package com.github.jurisliepins.client.state;

import com.github.jurisliepins.bitfield.ImmutableBitfield;
import com.github.jurisliepins.info.InfoHash;

public interface ImmutableClientStateTorrent {
    ClientStateTorrent.Status getStatus();

    InfoHash getInfoHash();

//    Object getPeerId();

    ImmutableBitfield getBitfield();

    String getName();

    long getLength();

    long getDownloaded();

    long getUploaded();

    long getLeft();

    double getDownloadRate();

    double getUploadRate();

}
