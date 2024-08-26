package com.github.jurisliepins.tracker.url;

public final class UrlEncoding {
    private UrlEncoding() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String requireSafe(final String value) {
        for (char c : value.toCharArray()) {
            if (!isSafeChar(c)) {
                throw new IllegalArgumentException("Character '%c' in '%s' is not safe".formatted(c, value));
            }
        }
        return value;
    }

    public static String encode(final byte[] bytes) {
        final StringBuilder ret = new StringBuilder();

        for (byte b : bytes) {
            if (isSafeChar((char) b)) {
                ret.append((char) b);
            } else {
                ret.append('%');
                ret.append(String.format("%02X", b));
            }
        }

        return ret.toString();
    }

    private static boolean isSafeChar(final char value) {
        if (value >= 'A' && value <= 'Z') {
            return true;
        }
        if (value >= 'a' && value <= 'z') {
            return true;
        }
        return switch (value) {
            case '-', '.', '_', '~' -> true;
            default -> false;
        };
    }
}
