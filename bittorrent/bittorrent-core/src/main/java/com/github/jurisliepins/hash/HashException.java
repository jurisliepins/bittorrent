package com.github.jurisliepins.hash;

public final class HashException extends RuntimeException {
    public HashException(final String message) {
        super(message);
    }

    public HashException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public HashException(final Throwable cause) {
        super(cause);
    }
}
