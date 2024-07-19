package com.github.jurisliepins.client;

import com.github.jurisliepins.ActorReceiver;
import com.github.jurisliepins.Envelope;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.info.File;
import com.github.jurisliepins.info.Info;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.info.MetaInfo;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

public final class Client implements ActorReceiver {

    private final ClientState state;

    public Client(final ClientState state) {
        this.state = Objects.requireNonNull(state, "state is null");
    }

    @Override
    public NextState receive(final Envelope envelope) {
        return switch (envelope) {
            case Envelope.Success success -> handleSuccess(success);
            case Envelope.Failure failure -> handleFailure(failure);
        };
    }

    private NextState handleSuccess(final Envelope.Success envelope) {
        return switch (envelope.message()) {
            case ClientCommand command -> handleCommand(envelope, command);
            case ClientRequest request -> handleRequest(envelope, request);
            default -> unhandled(envelope.message());
        };
    }

    private NextState handleCommand(final Envelope.Success envelope, final ClientCommand command) {
        switch (command) {
            case ClientCommand.Add addCommand -> {
                try {
                    switch (MetaInfo.fromBytes(addCommand.metaInfo())) {
                        case MetaInfo(
                                Info(
                                        int pieceLength,
                                        byte[] pieces,
                                        Boolean isPrivate,
                                        String name,
                                        Long length,
                                        String md5sum,
                                        List<File> files,
                                        InfoHash hash
                                ),
                                String announce,
                                List<List<String>> announceList,
                                OffsetDateTime creationDate,
                                String comment,
                                String createdBy,
                                String encoding
                        ) -> {
                            switch (state.torrents().get(hash)) {
                                case null -> envelope.sender()
                                        .post(new ClientCommandResult.Success(hash, "Torrent added"));
                                case Object torrent -> envelope.sender()
                                        .post(new ClientCommandResult.Failure(hash, "Torrent already exists"));
                            }
                        }
                        default -> throw new ClientException("Failed to read meta-info");
                    }
                } catch (Exception e) {
                    envelope.sender()
                            .post(new ClientCommandResult.Failure(
                                    InfoHash.BLANK,
                                    "Failed to add torrent with '%s'".formatted(e.getMessage())));
                }
            }
            case ClientCommand.Remove removeCommand -> {
                switch (state.torrents().get(removeCommand.infoHash())) {
                    case Object torrent -> {
                        envelope.sender()
                                .post(new ClientCommandResult.Success(removeCommand.infoHash(), "Torrent removed"));
                    }
                    case null -> envelope.sender()
                            .post(new ClientCommandResult.Failure(removeCommand.infoHash(), "Torrent not found"));
                }
            }
            case ClientCommand.Start startCommand -> {
                switch (state.torrents().get(startCommand.infoHash())) {
                    case Object torrent -> {
                        envelope.sender()
                                .post(new ClientCommandResult.Success(startCommand.infoHash(), "Torrent started"));
                    }
                    case null -> envelope.sender()
                            .post(new ClientCommandResult.Failure(startCommand.infoHash(), "Torrent not found"));
                }
            }
            case ClientCommand.Stop stopCommand -> {
                switch (state.torrents().get(stopCommand.infoHash())) {
                    case Object torrent -> {
                        envelope.sender()
                                .post(new ClientCommandResult.Success(stopCommand.infoHash(), "Torrent stopped"));
                    }
                    case null -> envelope.sender()
                            .post(new ClientCommandResult.Failure(stopCommand.infoHash(), "Torrent not found"));
                }
            }
        }
        return NextState.Receive;
    }

    private NextState handleRequest(final Envelope.Success envelope, final ClientRequest request) {
        switch (request) {
            case ClientRequest.Get getRequest -> {
                switch (state.torrents().get(getRequest.infoHash())) {
                    case Object torrent -> envelope.sender()
                            .post(new ClientResponse.Get(getRequest.infoHash()));
                    case null -> envelope.sender()
                            .post(new ClientResponse.Failure(getRequest.infoHash(), "Torrent not found"));
                }
            }
        }
        return NextState.Receive;
    }

    private NextState handleFailure(final Envelope.Failure envelope) {
        return NextState.Terminate;
    }

    private NextState unhandled(final Object message) {
        return NextState.Receive;
    }
}
