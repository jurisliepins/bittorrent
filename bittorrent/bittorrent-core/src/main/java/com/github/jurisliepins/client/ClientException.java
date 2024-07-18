package com.github.jurisliepins.client;

public final class ClientException extends RuntimeException {
    public ClientException(final String message) {
        super(message);
    }

    public ClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ClientException(final Throwable cause) {
        super(cause);
    }
}
