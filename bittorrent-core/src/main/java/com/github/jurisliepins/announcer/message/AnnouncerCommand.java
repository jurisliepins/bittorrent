package com.github.jurisliepins.announcer.message;

import com.github.jurisliepins.tracker.TrackerEventType;
import lombok.NonNull;

public sealed interface AnnouncerCommand permits
        AnnouncerCommand.Announce,
        AnnouncerCommand.Start,
        AnnouncerCommand.Stop,
        AnnouncerCommand.Terminate {

    record Announce(@NonNull TrackerEventType eventType) implements AnnouncerCommand { }

    record Start() implements AnnouncerCommand {
        public static final Start INSTANCE = new Start();
    }

    record Stop() implements AnnouncerCommand {
        public static final Stop INSTANCE = new Stop();
    }

    record Terminate() implements AnnouncerCommand {
        public static final Terminate INSTANCE = new Terminate();
    }
}
