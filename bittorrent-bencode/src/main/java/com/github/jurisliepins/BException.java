package com.github.jurisliepins;

public class BException extends RuntimeException {
    public BException(final String message) {
        super(message);
    }

    public BException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BException(final Throwable cause) {
        super(cause);
    }
}
