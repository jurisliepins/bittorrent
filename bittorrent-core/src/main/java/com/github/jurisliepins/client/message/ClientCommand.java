package com.github.jurisliepins.client.message;

import com.github.jurisliepins.info.InfoHash;
import lombok.NonNull;

public sealed interface ClientCommand permits
        ClientCommand.Add,
        ClientCommand.Remove,
        ClientCommand.Start,
        ClientCommand.Stop {

    record Add(byte @NonNull [] metaInfo) implements ClientCommand { }

    record Remove(@NonNull InfoHash infoHash) implements ClientCommand { }

    record Start(@NonNull InfoHash infoHash) implements ClientCommand { }

    record Stop(@NonNull InfoHash infoHash) implements ClientCommand { }
}
