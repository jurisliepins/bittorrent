package com.github.jurisliepins.torrent.message;

public sealed interface TorrentCommand permits
        TorrentCommand.Start,
        TorrentCommand.Stop,
        TorrentCommand.Terminate {

    record Start() implements TorrentCommand { }

    record Stop() implements TorrentCommand { }

    record Terminate() implements TorrentCommand { }
}
