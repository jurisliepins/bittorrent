package com.github.jurisliepins.repl;

import java.util.Objects;

public sealed interface ReplCommand permits
        ReplCommand.Add,
        ReplCommand.Remove,
        ReplCommand.Start,
        ReplCommand.Stop,
        ReplCommand.Exit {

    record Add(String path) implements ReplCommand {
        public Add {
            Objects.requireNonNull(path, "path is null");
        }

        public static final String VALUE = "add";
    }

    record Remove(String infoHash) implements ReplCommand {
        public Remove {
            Objects.requireNonNull(infoHash, "infoHash is null");
        }

        public static final String VALUE = "remove";
    }

    record Start(String infoHash) implements ReplCommand {
        public Start {
            Objects.requireNonNull(infoHash, "infoHash is null");
        }

        public static final String VALUE = "start";
    }

    record Stop(String infoHash) implements ReplCommand {
        public Stop {
            Objects.requireNonNull(infoHash, "infoHash is null");
        }

        public static final String VALUE = "stop";
    }

    record Exit() implements ReplCommand {
        public static final String VALUE = "exit";
    }
}
