package com.github.jurisliepins.repl;

public final class ReplException extends RuntimeException {
    public ReplException(final String message) {
        super(message);
    }

    public ReplException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ReplException(final Throwable cause) {
        super(cause);
    }
}
