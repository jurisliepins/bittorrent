package com.github.jurisliepins;

public final class CoreException extends RuntimeException {
    public CoreException(final String message) {
        super(message);
    }

    public CoreException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CoreException(final Throwable cause) {
        super(cause);
    }
}
