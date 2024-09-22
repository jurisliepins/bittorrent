package com.github.jurisliepins.client.handlers.request;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.client.message.ClientRequest;
import com.github.jurisliepins.client.message.ClientResponse;
import com.github.jurisliepins.context.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ClientRequestGetHandler implements CoreContextSuccessHandler<ClientState, ClientRequest.Get> {

    @Override
    public NextState handle(
            final Context context,
            final ClientState state,
            final Mailbox.Success mailbox,
            final ClientRequest.Get message) {
        switch (state.getTorrents().get(message.infoHash())) {
            case ClientState.Torrent torrent -> {
                var replyTorrent = ClientResponse.Torrent.builder()
                        .status(torrent.getStatus())
                        .infoHash(torrent.getInfoHash())
                        .selfId(torrent.getSelfId())
                        .bitfield(torrent.getBitfield())
                        .name(torrent.getName())
                        .pieceLength(torrent.getPieceLength())
                        .length(torrent.getLength())
                        .announce(torrent.getAnnounce())
                        .announceList(torrent.getAnnounceList())
                        .creationDate(torrent.getCreationDate())
                        .comment(torrent.getComment())
                        .createdBy(torrent.getCreatedBy())
                        .encoding(torrent.getEncoding())
                        .downloaded(torrent.getDownloaded())
                        .uploaded(torrent.getUploaded())
                        .left(torrent.getLeft())
                        .build();
                mailbox.reply(new ClientResponse.Get(replyTorrent));
            }
            case null -> mailbox.reply(new ClientResponse.Failure(message.infoHash(), "Torrent doesn't exist"));
        }
        return NextState.Receive;
    }
}
