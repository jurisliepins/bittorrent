package com.github.jurisliepins.announcer.message;

import com.github.jurisliepins.tracker.TrackerEventType;
import lombok.NonNull;

public sealed interface AnnouncerCommand permits
        AnnouncerCommand.Announce,
        AnnouncerCommand.Start,
        AnnouncerCommand.Stop,
        AnnouncerCommand.Terminate {

    record Announce(@NonNull TrackerEventType eventType) implements AnnouncerCommand { }

    record Start() implements AnnouncerCommand { }

    record Stop() implements AnnouncerCommand { }

    record Terminate() implements AnnouncerCommand { }
}
