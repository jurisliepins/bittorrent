package com.github.jurisliepins.client.handlers.command;

import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.client.message.ClientCommand;
import com.github.jurisliepins.client.message.ClientCommandResult;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ClientCommandStartHandler implements CoreContextSuccessHandler<ClientState, ClientCommand.Start> {

    @Override
    public NextState handle(
            final Context context,
            final ClientState state,
            final Mailbox.Success mailbox,
            final ClientCommand.Start message) {
        switch (state.getTorrents().get(message.infoHash())) {
            case ClientState.Torrent torrent -> {
                switch (torrent.getStatus()) {
                    case Stopped -> {
                        torrent.getRef().post(TorrentCommand.Start.INSTANCE, mailbox.self());
                        mailbox.reply(new ClientCommandResult.Success(torrent.getInfoHash(), "Torrent started"));
                    }
                    default -> mailbox.reply(new ClientCommandResult.Failure(torrent.getInfoHash(), "Torrent already started"));
                }
            }
            case null -> mailbox.reply(new ClientCommandResult.Failure(message.infoHash(), "Torrent doesn't exist"));
        }
        return NextState.Receive;
    }
}
