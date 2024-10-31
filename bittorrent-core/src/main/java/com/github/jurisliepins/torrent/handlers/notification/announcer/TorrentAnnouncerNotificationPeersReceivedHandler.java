package com.github.jurisliepins.torrent.handlers.notification.announcer;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.handshake.Handshake;
import com.github.jurisliepins.handshake.HandshakeConnection;
import com.github.jurisliepins.network.Connection;
import com.github.jurisliepins.torrent.TorrentState;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public final class TorrentAnnouncerNotificationPeersReceivedHandler
        implements CoreContextSuccessHandler<TorrentState, AnnouncerNotification.PeersReceived> {

    @Builder
    private record Step1(
            @NonNull InetSocketAddress address,
            @NonNull HandshakeConnection connection
    ) { }

    @Builder
    private record Step2(
            @NonNull InetSocketAddress address,
            @NonNull HandshakeConnection connection,
            @NonNull Handshake requestHandshake
    ) { }

    @Builder
    private record Step3(
            @NonNull InetSocketAddress address,
            @NonNull HandshakeConnection connection,
            @NonNull Handshake requestHandshake,
            @NonNull Handshake responseHandshake
    ) { }

    @Override
    public NextState handle(
            final Context context,
            final TorrentState state,
            final Mailbox.Success mailbox,
            final AnnouncerNotification.PeersReceived message) {
        log.info("[{}] Received peers {}", state.getInfoHash(), message.peers());

        var connections = message.peers()
                .parallelStream()
                .map(connect(context, state))
                .map(writeHandshake(state))
                .map(readHandshake(state))
                .map(verifyHandshake(state))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return NextState.Receive;
    }

    private static Function<InetSocketAddress, Optional<Step1>> connect(final Context context, final TorrentState state) {
        return address -> {
            try {
                var connection = context.io()
                        .connectionFactory()
                        .connect(address);
                log.info("[{}] Connected to peer {}", state.getInfoHash(), connection.remoteEndpoint());
                return Optional.of(
                        Step1.builder()
                                .address(address)
                                .connection(context.handshake()
                                                    .handshakeConnectionFactory()
                                                    .create(connection))
                                .build());
            } catch (Exception e) {
                log.error("[{}] Failed to connect to peer {}", state.getInfoHash(), address, e);
                return Optional.empty();
            }
        };
    }

    private static Function<Optional<Step1>, Optional<Step2>> writeHandshake(final TorrentState state) {
        return step1Opt -> step1Opt
                .flatMap(step1 -> {
                    try {
                        var handshake = Handshake.createDefault(
                                state.getInfoHash().toByteArray(),
                                state.getSelfId().toByteArray()
                        );
                        step1.connection().write(handshake);
                        log.info("[{}] Wrote handshake to peer {}", state.getInfoHash(), step1.address());
                        return Optional.of(
                                Step2.builder()
                                        .address(step1.address())
                                        .connection(step1.connection())
                                        .requestHandshake(handshake)
                                        .build()
                        );
                    } catch (Exception e) {
                        log.error("[{}] Failed to write handshake to peer {}", state.getInfoHash(), step1.address(), e);
                        return Optional.empty();
                    }
                });
    }

    private static Function<Optional<Step2>, Optional<Step3>> readHandshake(final TorrentState state) {
        return step2Opt -> step2Opt
                .flatMap(step2 -> {
                    try {
                        var handshake = step2.connection().read();
                        log.info("[{}] Read handshake from peer {}", state.getInfoHash(), step2.address());
                        return Optional.of(
                                Step3.builder()
                                        .address(step2.address())
                                        .connection(step2.connection())
                                        .requestHandshake(step2.requestHandshake())
                                        .responseHandshake(handshake)
                                        .build()
                        );
                    } catch (Exception e) {
                        log.error("[{}] Failed to read handshake from peer {}", state.getInfoHash(), step2.address(), e);
                        return Optional.empty();
                    }
                });
    }

    private static Function<Optional<Step3>, Connection> verifyHandshake(final TorrentState state) {
        return step3Opt -> step3Opt
                .map(step3 -> {
                    if (!Arrays.equals(step3.requestHandshake().protocol(), step3.responseHandshake().protocol())) {
                        log.error("[{}] Failed to verify handshake with peer {} - protocols don't match",
                                  state.getInfoHash(),
                                  step3.address());
                        return null;
                    }
                    if (!Arrays.equals(step3.requestHandshake().infoHash(), step3.responseHandshake().infoHash())) {
                        log.error("[{}] Failed to verify handshake with peer {} - info-hashes don't match",
                                  state.getInfoHash(),
                                  step3.address());
                        return null;
                    }
                    log.info("[{}] Verified handshake with peer {}", state.getInfoHash(), step3.address());
                    return step3.connection().connection();
                })
                .orElse(null);
    }
}
