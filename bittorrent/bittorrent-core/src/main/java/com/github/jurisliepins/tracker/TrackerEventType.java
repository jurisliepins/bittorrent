package com.github.jurisliepins.tracker;

public enum TrackerEventType {
    Started,
    Stopped,
    Completed;

    @Override
    public String toString() {
        return switch (this) {
            case Started -> "started";
            case Stopped -> "stopped";
            case Completed -> "completed";
        };
    }
}
