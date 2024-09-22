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
public final class ClientCommandRemoveHandler implements CoreContextSuccessHandler<ClientState, ClientCommand.Remove> {

    @Override
    public NextState handle(
            final Context context,
            final ClientState state,
            final Mailbox.Success mailbox,
            final ClientCommand.Remove message) {
        switch (state.getTorrents().remove(message.infoHash())) {
            case ClientState.Torrent torrent -> {
                torrent.getRef().post(TorrentCommand.Terminate.INSTANCE, mailbox.self());
                mailbox.reply(new ClientCommandResult.Success(message.infoHash(), "Torrent removed"));
            }
            case null -> mailbox.reply(new ClientCommandResult.Failure(message.infoHash(), "Torrent doesn't exist"));
        }
        return NextState.Receive;
    }
}
