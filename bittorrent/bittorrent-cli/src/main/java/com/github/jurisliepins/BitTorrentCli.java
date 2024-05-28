package com.github.jurisliepins;

import com.github.jurisliepins.repl.Repl;

public final class BitTorrentCli {
    private BitTorrentCli() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static void main(final String[] args) {
        new Repl().run();
    }
}
