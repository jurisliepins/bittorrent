package com.github.jurisliepins.repl;

import com.github.jurisliepins.BObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public final class Repl {

    private final BObjectMapper mapper = new BObjectMapper();

    public void run() {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                try {
                    System.out.println(">>> ");
                    switch (parseCommand(reader.readLine())) {
                        case ReplCommand.Add command -> add(command);
                        case ReplCommand.Remove command -> remove(command);
                        case ReplCommand.Start command -> start(command);
                        case ReplCommand.Stop command -> stop(command);
                        case ReplCommand.Exit command -> {
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        } catch (Exception e) {
            throw new ReplException("Failed to run REPL.", e);
        }
    }

    private ReplCommand parseCommand(final String command) {
        final String[] parts = command.trim().split("\\s+");
        return switch (parts[0]) {
            case ReplCommand.Add.VALUE -> new ReplCommand.Add(parts[1]);
            case ReplCommand.Remove.VALUE -> new ReplCommand.Remove(parts[1]);
            case ReplCommand.Start.VALUE -> new ReplCommand.Start(parts[1]);
            case ReplCommand.Stop.VALUE -> new ReplCommand.Stop(parts[1]);
            case ReplCommand.Exit.VALUE -> new ReplCommand.Exit();
            default -> throw new ReplException("Unknown command '%s'.".formatted(command));
        };
    }

    private void add(final ReplCommand.Add command) {
        System.out.println("add " + command.path());
    }

    private void remove(final ReplCommand.Remove command) {
        System.out.println("remove " + command.infoHash());
    }

    private void start(final ReplCommand.Start command) {
        System.out.println("start " + command.infoHash());
    }

    private void stop(final ReplCommand.Stop command) {
        System.out.println("stop " + command.infoHash());
    }
}
