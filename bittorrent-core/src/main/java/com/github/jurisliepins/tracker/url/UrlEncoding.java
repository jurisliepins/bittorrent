package com.github.jurisliepins.tracker.url;

public final class UrlEncoding {
    private UrlEncoding() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String requireSafe(final String value) {
        for (var c : value.toCharArray()) {
            if (!isSafeChar(c)) {
                throw new IllegalArgumentException("Character '%c' in '%s' is not safe".formatted(c, value));
            }
        }
        return value;
    }

    public static String encode(final byte[] bytes) {
        var ret = new StringBuilder();

        for (var b : bytes) {
            if (isSafeChar((char) b)) {
                ret.append((char) b);
            } else {
                ret.append('%');
                ret.append(String.format("%02X", b));
            }
        }

        return ret.toString();
    }

    private static boolean isSafeChar(final Character value) {
        return switch (value) {
            case Character val when val >= 'A' && val <= 'Z' -> true;
            case Character val when val >= 'a' && val <= 'z' -> true;
            case '-', '.', '_', '~' -> true;
            default -> false;
        };
    }
}
