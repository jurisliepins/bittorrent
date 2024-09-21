package com.github.jurisliepins.announcer;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.MailboxReceiver;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.tracker.TrackerEventType;
import com.github.jurisliepins.tracker.TrackerRequest;
import com.github.jurisliepins.tracker.TrackerResponse;
import com.github.jurisliepins.types.StatusType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class AnnouncerMailboxReceiver implements MailboxReceiver {
    private final Context context;
    private final AnnouncerState state;
    private final ActorRef notifiedRef;

    public AnnouncerMailboxReceiver(
            @NonNull final Context context,
            @NonNull final AnnouncerState state,
            @NonNull final ActorRef notifiedRef) {
        this.context = context;
        this.state = state;
        this.notifiedRef = notifiedRef;
    }

    @Override
    public NextState receive(final Mailbox mailbox) {
        return switch (mailbox) {
            case Mailbox.Success m -> handle(m);
            case Mailbox.Failure m -> handle(m);
        };
    }

    private NextState handle(final Mailbox.Success mailbox) {
        return switch (mailbox.message()) {
            case AnnouncerCommand command -> handle(mailbox, command);
            default -> unhandled(mailbox);
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerCommand command) {
        return switch (command) {
            case AnnouncerCommand.Announce announce -> handle(mailbox, announce);
            case AnnouncerCommand.Start start -> handle(mailbox, start);
            case AnnouncerCommand.Stop stop -> handle(mailbox, stop);
            case AnnouncerCommand.Terminate terminate -> handle(mailbox, terminate);
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerCommand.Announce command) {
        log.info("[{}] Announcing '{}' on '{}'", state.getInfoHash(), command.eventTypeOpt(), state.getAnnounce());
        var response = context.trackerClient().announce(
                TrackerRequest.builder(state.getAnnounce())
                        .parameter(TrackerRequest.INFO_HASH, state.getInfoHash().toByteArray())
                        .parameter(TrackerRequest.PEER_ID, state.getSelfPeerId().toString())
                        .parameter(TrackerRequest.PORT, state.getPort())
                        .parameter(TrackerRequest.DOWNLOADED, state.getDownloaded())
                        .parameter(TrackerRequest.UPLOADED, state.getUploaded())
                        .parameter(TrackerRequest.LEFT, state.getLeft())
                        .parameter(TrackerRequest.EVENT, command.eventTypeOpt()
                                .map(TrackerEventType::toString)
                                .orElse(null))
                        .parameter(TrackerRequest.COMPACT, 1)
                        .parameter(TrackerRequest.NO_PEER_ID, 1)
                        .parameter(TrackerRequest.NUM_WANT, state.getPeerCount())
                        .toQuery());
        switch (response) {
            case TrackerResponse.Success success -> {
                log.info("[{}] Announced successfully on '{}' with '{}'", state.getInfoHash(), state.getAnnounce(), success);
                switch (state.getStatus()) {
                    case Started -> {
                        log.info("[{}] Scheduling re-announce on '{}' in {}s",
                                 state.getInfoHash(),
                                 state.getAnnounce(),
                                 Math.max(success.interval(), state.getIntervalSeconds()));
                        mailbox.system()
                                .schedulePostOnce(
                                        Math.max(success.interval(), state.getIntervalSeconds()),
                                        TimeUnit.SECONDS,
                                        mailbox.self(),
                                        new AnnouncerCommand.Announce(Optional.empty()));
                        notifiedRef.post(new AnnouncerNotification.PeersReceived(state.getInfoHash(), success.peers()), mailbox.self());
                        return NextState.Receive;
                    }
                    default -> {
                        log.info("[{}] Not scheduling re-announce since we're no longer running", state.getInfoHash());
                        return NextState.Receive;
                    }
                }
            }

            case TrackerResponse.Failure failure -> {
                log.error("[{}] Announced with failure response on '{}' with '{}'",
                          state.getInfoHash(),
                          state.getAnnounce(),
                          failure);
                return NextState.Receive;
            }
        }
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerCommand.Start command) {
        return switch (state.getStatus()) {
            case Stopped -> {
                mailbox.self().post(new AnnouncerCommand.Announce(Optional.of(TrackerEventType.Started)), mailbox.self());
                state.setStatus(StatusType.Started);
                notifiedRef.post(new AnnouncerNotification.StatusChanged(state.getInfoHash(), StatusType.Started), mailbox.self());
                yield NextState.Receive;
            }

            default -> {
                log.info("[{}] Announcer already started", state.getInfoHash());
                yield NextState.Receive;
            }
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerCommand.Stop command) {
        return switch (state.getStatus()) {
            case Started -> {
                mailbox.self().post(new AnnouncerCommand.Announce(Optional.of(TrackerEventType.Stopped)), mailbox.self());
                state.setStatus(StatusType.Stopped);
                notifiedRef.post(new AnnouncerNotification.StatusChanged(state.getInfoHash(), StatusType.Stopped), mailbox.self());
                yield NextState.Receive;
            }

            default -> {
                log.info("[{}] Announcer already stopped", state.getInfoHash());
                yield NextState.Receive;
            }
        };
    }

    private NextState handle(final Mailbox.Success mailbox, final AnnouncerCommand.Terminate command) {
        notifiedRef.post(new AnnouncerNotification.Terminated(state.getInfoHash()), mailbox.self());
        return NextState.Terminate;
    }

    private NextState handle(final Mailbox.Failure mailbox) {
        return switch (mailbox.message()) {
            case AnnouncerCommand command -> {
                log.error("[{}] Failed to handle command", state.getInfoHash(), mailbox.cause());
                notifiedRef.post(new AnnouncerNotification.Failure(state.getInfoHash(), mailbox.cause()), mailbox.self());
                yield NextState.Receive;
            }

            default -> {
                log.error("[{}] Failed", state.getInfoHash(), mailbox.cause());
                notifiedRef.post(new AnnouncerNotification.Failure(state.getInfoHash(), mailbox.cause()), mailbox.self());
                yield NextState.Receive;
            }
        };
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        log.error("[{}] Unhandled message {}", state.getInfoHash(), mailbox.message());
        return NextState.Receive;
    }
}
