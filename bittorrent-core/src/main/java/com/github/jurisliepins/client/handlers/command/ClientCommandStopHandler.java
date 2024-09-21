package com.github.jurisliepins.client.handlers.command;

import com.github.jurisliepins.CoreContextSuccessHandler;
import com.github.jurisliepins.Mailbox;
import com.github.jurisliepins.NextState;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.client.message.ClientCommand;
import com.github.jurisliepins.client.message.ClientCommandResult;
import com.github.jurisliepins.context.Context;
import com.github.jurisliepins.torrent.message.TorrentCommand;

public final class ClientCommandStopHandler implements CoreContextSuccessHandler<ClientState, ClientCommand.Stop> {

    @Override
    public NextState handle(
            final Context context,
            final Mailbox.Success mailbox,
            final ClientState state,
            final ClientCommand.Stop message) {
        switch (state.getTorrents().get(message.infoHash())) {
            case ClientState.Torrent torrent -> {
                switch (torrent.getStatus()) {
                    case Started, Errored -> {
                        torrent.getRef()
                                .post(TorrentCommand.Stop.INSTANCE, mailbox.self());
                        mailbox.reply(new ClientCommandResult.Success(torrent.getInfoHash(), "Torrent stopped"));
                    }
                    default -> {
                        mailbox.reply(new ClientCommandResult.Failure(torrent.getInfoHash(), "Torrent already stopped"));
                    }
                }
            }
            case null -> {
                mailbox.reply(new ClientCommandResult.Failure(message.infoHash(), "Torrent doesn't exist"));
            }
        }
        return NextState.Receive;
    }

}
