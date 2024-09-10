package com.github.jurisliepins.torrent.message;

public sealed interface TorrentCommand permits
        TorrentCommand.Start,
        TorrentCommand.Stop,
        TorrentCommand.Terminate {

    record Start() implements TorrentCommand {
        public static final Start INSTANCE = new Start();
    }

    record Stop() implements TorrentCommand {
        public static final Stop INSTANCE = new Stop();
    }

    record Terminate() implements TorrentCommand {
        public static final Terminate INSTANCE = new Terminate();
    }
}
