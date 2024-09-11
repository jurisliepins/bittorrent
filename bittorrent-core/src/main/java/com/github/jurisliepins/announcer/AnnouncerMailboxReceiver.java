package com.github.jurisliepins.announcer;

import com.github.jurisliepins.ActorRef;
import com.github.jurisliepins.CoreMailboxNotifiedStateContextLoggingReceiver;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.tracker.TrackerEventType;
import com.github.jurisliepins.tracker.TrackerRequest;
import com.github.jurisliepins.tracker.TrackerRequestBuilder;
import com.github.jurisliepins.tracker.TrackerResponse;
import com.github.jurisliepins.types.StatusType;
import lombok.NonNull;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class AnnouncerMailboxReceiver extends CoreMailboxNotifiedStateContextLoggingReceiver<AnnouncerState, AnnouncerNotification> {

    public AnnouncerMailboxReceiver(
            @NonNull final Context context,
            @NonNull final AnnouncerState state,
            @NonNull final ActorRef notifiedRef) {
        super(context, state, notifiedRef);
    }

    @Override
    public NextState receive(final Mailbox mailbox) {
        return switch (mailbox) {
            case Mailbox.Success mailboxSuccess -> handleSuccess(mailboxSuccess);
            case Mailbox.Failure mailboxFailure -> handleFailure(mailboxFailure);
        };
    }

    private NextState handleSuccess(final Mailbox.Success mailbox) {
        return switch (mailbox.message()) {
            case AnnouncerCommand command -> handleCommand(mailbox, command);
            default -> unhandled(mailbox);
        };
    }

    private NextState handleCommand(final Mailbox.Success mailbox, final AnnouncerCommand command) {
        return switch (command) {
            case AnnouncerCommand.Announce announce -> handleAnnounceCommand(mailbox, announce);
            case AnnouncerCommand.Start start -> handleStartCommand(mailbox, start);
            case AnnouncerCommand.Stop stop -> handleStopCommand(mailbox, stop);
            case AnnouncerCommand.Terminate terminate -> handleTerminateCommand(mailbox, terminate);
        };
    }

    private NextState handleAnnounceCommand(final Mailbox.Success mailbox, final AnnouncerCommand.Announce command) {
        logger().info("[{}] Announcing '{}' on '{}'", state().getInfoHash(), command.eventTypeOpt(), state().getAnnounce());
        var response = context().trackerClient().announce(
                new TrackerRequestBuilder(state().getAnnounce())
                        .parameter(TrackerRequest.INFO_HASH, state().getInfoHash().toByteArray())
                        .parameter(TrackerRequest.PEER_ID, state().getSelfPeerId().toString())
                        .parameter(TrackerRequest.PORT, state().getPort())
                        .parameter(TrackerRequest.DOWNLOADED, state().getDownloaded())
                        .parameter(TrackerRequest.UPLOADED, state().getUploaded())
                        .parameter(TrackerRequest.LEFT, state().getLeft())
                        .parameter(TrackerRequest.EVENT, command.eventTypeOpt()
                                .map(TrackerEventType::toString)
                                .orElse(null))
                        .parameter(TrackerRequest.COMPACT, 1)
                        .parameter(TrackerRequest.NO_PEER_ID, 1)
                        .parameter(TrackerRequest.NUM_WANT, state().getPeerCount())
                        .toQuery());
        switch (response) {
            case TrackerResponse.Success success -> {
                logger().info("[{}] Announced successfully on '{}' with '{}'", state().getInfoHash(), state().getAnnounce(), success);
                switch (state().getStatus()) {
                    case Started -> {
                        logger().info("[{}] Scheduling re-announce on '{}' in {}s",
                                      state().getInfoHash(),
                                      state().getAnnounce(),
                                      Math.max(success.interval(), state().getIntervalSeconds()));
                        mailbox.system()
                                .schedulePostOnce(
                                        Math.max(success.interval(), state().getIntervalSeconds()),
                                        TimeUnit.SECONDS,
                                        mailbox.self(),
                                        new AnnouncerCommand.Announce(Optional.empty()));
                        return receiveNext(new AnnouncerNotification.PeersReceived(state().getInfoHash(), success.peers()));
                    }
                    default -> {
                        logger().info("[{}] Not scheduling re-announce since we're no longer running", state().getInfoHash());
                        return receiveNext();
                    }
                }
            }

            case TrackerResponse.Failure failure -> {
                logger().error("[{}] Announced with failure response on '{}' with '{}'",
                               state().getInfoHash(),
                               state().getAnnounce(),
                               failure);
                return receiveNext();
            }
        }
    }

    private NextState handleStartCommand(final Mailbox.Success mailbox, final AnnouncerCommand.Start command) {
        return switch (state().getStatus()) {
            case Stopped -> {
                mailbox.self().post(new AnnouncerCommand.Announce(Optional.of(TrackerEventType.Started)), mailbox.self());
                yield receiveNext(StatusType.Started, new AnnouncerNotification.StatusChanged(state().getInfoHash(), StatusType.Started));
            }

            default -> {
                logger().info("[{}] Announcer already started", state().getInfoHash());
                yield receiveNext();
            }
        };
    }

    private NextState handleStopCommand(final Mailbox.Success mailbox, final AnnouncerCommand.Stop command) {
        return switch (state().getStatus()) {
            case Started -> {
                mailbox.self().post(new AnnouncerCommand.Announce(Optional.of(TrackerEventType.Stopped)), mailbox.self());
                yield receiveNext(StatusType.Stopped, new AnnouncerNotification.StatusChanged(state().getInfoHash(), StatusType.Stopped));
            }

            default -> {
                logger().info("[{}] Announcer already stopped", state().getInfoHash());
                yield receiveNext();
            }
        };
    }

    private NextState handleTerminateCommand(final Mailbox.Success mailbox, final AnnouncerCommand.Terminate command) {
        return terminate(new AnnouncerNotification.Terminated(state().getInfoHash()));
    }

    private NextState handleFailure(final Mailbox.Failure mailbox) {
        return switch (mailbox.message()) {
            case AnnouncerCommand command -> {
                logger().error("[{}] Failed to handle command", state().getInfoHash(), mailbox.cause());
                yield receiveNext(new AnnouncerNotification.Failure(state().getInfoHash(), mailbox.cause()));
            }

            default -> {
                logger().error("[{}] Failed", state().getInfoHash(), mailbox.cause());
                yield receiveNext(new AnnouncerNotification.Failure(state().getInfoHash(), mailbox.cause()));
            }
        };
    }

    private NextState unhandled(final Mailbox.Success mailbox) {
        logger().error("[{}] Unhandled message {}", state().getInfoHash(), mailbox.message());
        return receiveNext();
    }
}
