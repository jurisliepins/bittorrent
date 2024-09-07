package com.github.jurisliepins.client.message;

import com.github.jurisliepins.info.InfoHash;
import lombok.NonNull;

public sealed interface ClientRequest permits ClientRequest.Get {

    record Get(@NonNull InfoHash infoHash) implements ClientRequest { }

}
