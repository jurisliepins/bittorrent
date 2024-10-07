package com.github.jurisliepins.announcer.handlers.command;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.AnnouncerState;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.tracker.TrackerRequest;
import com.github.jurisliepins.tracker.TrackerResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class AnnouncerCommandAnnounceHandler implements CoreContextSuccessHandler<AnnouncerState, AnnouncerCommand.Announce> {

    @Override
    public NextState handle(
            final Context context,
            final AnnouncerState state,
            final Mailbox.Success mailbox,
            final AnnouncerCommand.Announce message) {
        log.info("[{}] Announcing '{}' on '{}'", state.getInfoHash(), message.eventTypeOpt(), state.getAnnounce());

        var response = context.tracker()
                .client()
                .announce(TrackerRequest.builder(state.getAnnounce())
                                  .infoHash(state.getInfoHash())
                                  .peerId(state.getSelfId())
                                  .port(state.getPort())
                                  .downloaded(state.getDownloaded())
                                  .uploaded(state.getUploaded())
                                  .left(state.getLeft())
                                  .event(message.eventTypeOpt())
                                  .compact(1)
                                  .noPeerId(1)
                                  .numWant(state.getPeerCount())
                                  .toQuery());
        switch (response) {
            case TrackerResponse.Success success -> {
                log.info("[{}] Announced successfully on '{}' with '{}'", state.getInfoHash(), state.getAnnounce(), success);

                switch (state.getStatus()) {
                    case Started, Errored -> {
                        log.info("[{}] Scheduling re-announce on '{}' in {}s",
                                 state.getInfoHash(),
                                 state.getAnnounce(),
                                 success.interval());

                        mailbox.system()
                                .schedulePostOnce(success.interval(),
                                                  TimeUnit.SECONDS,
                                                  mailbox.self(),
                                                  new AnnouncerCommand.Announce(Optional.empty()));
                        state.getNotifiedRef()
                                .post(new AnnouncerNotification.PeersReceived(state.getInfoHash(), success.peers()), mailbox.self());
                    }
                    default -> log.info("[{}] Not scheduling re-announce since we're no longer running", state.getInfoHash());
                }
            }

            case TrackerResponse.Failure failure ->
                    log.error("[{}] Announced with failure response on '{}' with '{}'", state.getInfoHash(), state.getAnnounce(), failure);
        }
        return NextState.Receive;
    }
}
