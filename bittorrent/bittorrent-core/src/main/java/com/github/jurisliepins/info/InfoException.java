package com.github.jurisliepins.info;

public final class InfoException extends RuntimeException {
    public InfoException(final String message) {
        super(message);
    }

    public InfoException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InfoException(final Throwable cause) {
        super(cause);
    }
}
