package com.github.jurisliepins.tracker;

public enum TrackerEventType {
    STARTED,
    STOPPED,
    COMPLETED;

    @Override
    public String toString() {
        return switch (this) {
            case STARTED -> "started";
            case STOPPED -> "stopped";
            case COMPLETED -> "completed";
        };
    }
}
