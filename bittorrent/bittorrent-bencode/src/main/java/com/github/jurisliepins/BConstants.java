package com.github.jurisliepins;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class BConstants {
    private BConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static final Charset DEFAULT_ENCODING = StandardCharsets.ISO_8859_1;
}
