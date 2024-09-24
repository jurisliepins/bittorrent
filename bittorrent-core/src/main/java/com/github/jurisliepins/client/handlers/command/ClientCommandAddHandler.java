package com.github.jurisliepins.client.handlers.command;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.announcer.AnnouncerMailboxReceiver;
import com.github.jurisliepins.announcer.AnnouncerState;
import com.github.jurisliepins.bitfield.Bitfield;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.client.message.ClientCommand;
import com.github.jurisliepins.client.message.ClientCommandResult;
import com.github.jurisliepins.common.StatusType;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.info.MetaInfoMapper;
import com.github.jurisliepins.peer.Id;
import com.github.jurisliepins.torrent.TorrentMailboxReceiver;
import com.github.jurisliepins.torrent.TorrentState;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ClientCommandAddHandler implements CoreContextSuccessHandler<ClientState, ClientCommand.Add> {

    @Override
    public NextState handle(
            final Context context,
            final ClientState state,
            final Mailbox.Success mailbox,
            final ClientCommand.Add message) {
        var mi = MetaInfoMapper.fromBytes(message.metaInfo());

        switch (state.getTorrents().get(mi.info().hash())) {
            case null -> {
                var announcerRef = mailbox.system()
                        .spawn(new AnnouncerMailboxReceiver(
                                context,
                                AnnouncerState.builder()
                                        .status(StatusType.Stopped)
                                        .infoHash(mi.info().hash())
                                        .selfId(Id.self())
                                        .announce(mi.announce())
                                        .announceList(mi.announceList())
                                        .peerCount(state.getSettings().peerCount())
                                        .port(state.getSettings().port())
                                        .intervalSeconds(state.getSettings().intervalSeconds())
                                        .downloaded(0L)
                                        .uploaded(0L)
                                        .left(mi.info().length())
                                        .notifiedRef(mailbox.self())
                                        .build()));
                var torrentRef = mailbox.system()
                        .spawn(new TorrentMailboxReceiver(
                                context,
                                TorrentState.builder()
                                        .status(StatusType.Stopped)
                                        .infoHash(mi.info().hash())
                                        .selfId(Id.self())
                                        .bitfield(Bitfield.BLANK)
                                        .name(mi.info().name())
                                        .pieceLength(mi.info().pieceLength())
                                        .length(mi.info().length())
                                        .announce(mi.announce())
                                        .announceList(mi.announceList())
                                        .creationDate(mi.creationDate())
                                        .comment(mi.comment())
                                        .createdBy(mi.createdBy())
                                        .encoding(mi.encoding())
                                        .downloaded(0L)
                                        .uploaded(0L)
                                        .left(mi.info().length())
                                        .announcerRef(announcerRef)
                                        .notifiedRef(mailbox.self())
                                        .build()));
                state.getTorrents()
                        .add(ClientState.Torrent.builder()
                                     .ref(torrentRef)
                                     .status(StatusType.Stopped)
                                     .infoHash(mi.info().hash())
                                     .selfId(Id.self())
                                     .bitfield(Bitfield.BLANK)
                                     .name(mi.info().name())
                                     .pieceLength(mi.info().pieceLength())
                                     .length(mi.info().length())
                                     .announce(mi.announce())
                                     .announceList(mi.announceList())
                                     .creationDate(mi.creationDate())
                                     .comment(mi.comment())
                                     .createdBy(mi.createdBy())
                                     .encoding(mi.encoding())
                                     .downloaded(0L)
                                     .uploaded(0L)
                                     .left(mi.info().length())
                                     .build());
                mailbox.reply(new ClientCommandResult.Success(mi.info().hash(), "Torrent added"));
            }
            case ClientState.Torrent ignored -> mailbox.reply(new ClientCommandResult.Failure(mi.info().hash(), "Torrent already exists"));
        }
        return NextState.Receive;
    }
}
