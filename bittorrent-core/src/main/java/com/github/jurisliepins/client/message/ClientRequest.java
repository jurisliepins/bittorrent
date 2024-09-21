package com.github.jurisliepins.client.message;

import com.github.jurisliepins.info.Hash;
import lombok.NonNull;

public sealed interface ClientRequest permits ClientRequest.Get {

    record Get(@NonNull Hash infoHash) implements ClientRequest { }

}
