package com.github.jurisliepins.context;

import com.github.jurisliepins.handler.CoreContextFailureHandler;
import com.github.jurisliepins.handler.CoreContextSuccessHandler;
import com.github.jurisliepins.announcer.AnnouncerState;
import com.github.jurisliepins.announcer.handlers.AnnouncerFailureHandler;
import com.github.jurisliepins.announcer.handlers.command.AnnouncerCommandAnnounceHandler;
import com.github.jurisliepins.announcer.handlers.command.AnnouncerCommandStartHandler;
import com.github.jurisliepins.announcer.handlers.command.AnnouncerCommandStopHandler;
import com.github.jurisliepins.announcer.handlers.command.AnnouncerCommandTerminateHandler;
import com.github.jurisliepins.announcer.message.AnnouncerCommand;
import com.github.jurisliepins.announcer.message.AnnouncerNotification;
import com.github.jurisliepins.client.ClientState;
import com.github.jurisliepins.client.handlers.ClientFailureHandler;
import com.github.jurisliepins.client.handlers.command.ClientCommandAddHandler;
import com.github.jurisliepins.client.handlers.command.ClientCommandRemoveHandler;
import com.github.jurisliepins.client.handlers.command.ClientCommandStartHandler;
import com.github.jurisliepins.client.handlers.command.ClientCommandStopHandler;
import com.github.jurisliepins.client.handlers.notification.announcer.ClientAnnouncerNotificationFailureHandler;
import com.github.jurisliepins.client.handlers.notification.announcer.ClientAnnouncerNotificationPeersReceivedHandler;
import com.github.jurisliepins.client.handlers.notification.announcer.ClientAnnouncerNotificationStatusChangedHandler;
import com.github.jurisliepins.client.handlers.notification.announcer.ClientAnnouncerNotificationTerminatedHandler;
import com.github.jurisliepins.client.handlers.notification.torrent.ClientTorrentNotificationFailureHandler;
import com.github.jurisliepins.client.handlers.notification.torrent.ClientTorrentNotificationStatusChangedHandler;
import com.github.jurisliepins.client.handlers.notification.torrent.ClientTorrentNotificationTerminatedHandler;
import com.github.jurisliepins.client.handlers.request.ClientRequestGetHandler;
import com.github.jurisliepins.client.message.ClientCommand;
import com.github.jurisliepins.client.message.ClientRequest;
import com.github.jurisliepins.torrent.TorrentState;
import com.github.jurisliepins.torrent.handlers.TorrentFailureHandler;
import com.github.jurisliepins.torrent.handlers.command.TorrentCommandStartHandler;
import com.github.jurisliepins.torrent.handlers.command.TorrentCommandStopHandler;
import com.github.jurisliepins.torrent.handlers.command.TorrentCommandTerminateHandler;
import com.github.jurisliepins.torrent.handlers.notification.announcer.TorrentAnnouncerNotificationFailureHandler;
import com.github.jurisliepins.torrent.handlers.notification.announcer.TorrentAnnouncerNotificationPeersReceivedHandler;
import com.github.jurisliepins.torrent.handlers.notification.announcer.TorrentAnnouncerNotificationStatusChangedHandler;
import com.github.jurisliepins.torrent.handlers.notification.announcer.TorrentAnnouncerNotificationTerminatedHandler;
import com.github.jurisliepins.torrent.message.TorrentCommand;
import com.github.jurisliepins.torrent.message.TorrentNotification;
import com.github.jurisliepins.tracker.TrackerClient;
import com.github.jurisliepins.tracker.TrackerClientImpl;
import lombok.NonNull;

public record Context(
        @NonNull IO io,
        @NonNull Handlers handlers
) {
    public record IO(
            @NonNull TrackerClient trackerClient
    ) { }

    public record Handlers(
            @NonNull Handlers.Announcer announcer,
            @NonNull Handlers.Torrent torrent,
            @NonNull Handlers.Client client
    ) {
        public record Announcer(
                @NonNull Handlers.Announcer.Command command,
                @NonNull CoreContextFailureHandler<AnnouncerState> failure
        ) {
            public record Command(
                    @NonNull CoreContextSuccessHandler<AnnouncerState, AnnouncerCommand.Announce> announce,
                    @NonNull CoreContextSuccessHandler<AnnouncerState, AnnouncerCommand.Start> start,
                    @NonNull CoreContextSuccessHandler<AnnouncerState, AnnouncerCommand.Stop> stop,
                    @NonNull CoreContextSuccessHandler<AnnouncerState, AnnouncerCommand.Terminate> terminate
            ) { }
        }

        public record Torrent(
                @NonNull Handlers.Torrent.Command command,
                @NonNull Handlers.Torrent.Notification notification,
                @NonNull CoreContextFailureHandler<TorrentState> failure
        ) {
            public record Command(
                    @NonNull CoreContextSuccessHandler<TorrentState, TorrentCommand.Start> start,
                    @NonNull CoreContextSuccessHandler<TorrentState, TorrentCommand.Stop> stop,
                    @NonNull CoreContextSuccessHandler<TorrentState, TorrentCommand.Terminate> terminate
            ) { }

            public record Notification(
                    @NonNull Torrent.Notification.Announcer announcer
            ) {
                public record Announcer(
                        @NonNull CoreContextSuccessHandler<TorrentState, AnnouncerNotification.PeersReceived> peersReceived,
                        @NonNull CoreContextSuccessHandler<TorrentState, AnnouncerNotification.StatusChanged> statusChanged,
                        @NonNull CoreContextSuccessHandler<TorrentState, AnnouncerNotification.Terminated> terminated,
                        @NonNull CoreContextSuccessHandler<TorrentState, AnnouncerNotification.Failure> failure
                ) { }
            }
        }

        public record Client(
                @NonNull Handlers.Client.Command command,
                @NonNull Handlers.Client.Request request,
                @NonNull Handlers.Client.Notification notification,
                @NonNull CoreContextFailureHandler<ClientState> failure
        ) {
            public record Command(
                    @NonNull CoreContextSuccessHandler<ClientState, ClientCommand.Add> add,
                    @NonNull CoreContextSuccessHandler<ClientState, ClientCommand.Remove> remove,
                    @NonNull CoreContextSuccessHandler<ClientState, ClientCommand.Start> start,
                    @NonNull CoreContextSuccessHandler<ClientState, ClientCommand.Stop> stop
            ) { }

            public record Request(
                    @NonNull CoreContextSuccessHandler<ClientState, ClientRequest.Get> get
            ) { }

            public record Notification(
                    @NonNull Client.Notification.Torrent torrent,
                    @NonNull Client.Notification.Announcer announcer
            ) {
                public record Torrent(
                        @NonNull CoreContextSuccessHandler<ClientState, TorrentNotification.StatusChanged> statusChanged,
                        @NonNull CoreContextSuccessHandler<ClientState, TorrentNotification.Terminated> terminated,
                        @NonNull CoreContextSuccessHandler<ClientState, TorrentNotification.Failure> failure
                ) { }

                public record Announcer(
                        @NonNull CoreContextSuccessHandler<ClientState, AnnouncerNotification.PeersReceived> peersReceived,
                        @NonNull CoreContextSuccessHandler<ClientState, AnnouncerNotification.StatusChanged> statusChanged,
                        @NonNull CoreContextSuccessHandler<ClientState, AnnouncerNotification.Terminated> terminated,
                        @NonNull CoreContextSuccessHandler<ClientState, AnnouncerNotification.Failure> failure
                ) { }
            }
        }
    }

    public static Context defaultContext() {
        return new Context(
                new IO(
                        new TrackerClientImpl()
                ),
                new Handlers(
                        new Handlers.Announcer(
                                new Handlers.Announcer.Command(
                                        new AnnouncerCommandAnnounceHandler(),
                                        new AnnouncerCommandStartHandler(),
                                        new AnnouncerCommandStopHandler(),
                                        new AnnouncerCommandTerminateHandler()
                                ),
                                new AnnouncerFailureHandler()
                        ),
                        new Handlers.Torrent(
                                new Handlers.Torrent.Command(
                                        new TorrentCommandStartHandler(),
                                        new TorrentCommandStopHandler(),
                                        new TorrentCommandTerminateHandler()
                                ),
                                new Handlers.Torrent.Notification(
                                        new Handlers.Torrent.Notification.Announcer(
                                                new TorrentAnnouncerNotificationPeersReceivedHandler(),
                                                new TorrentAnnouncerNotificationStatusChangedHandler(),
                                                new TorrentAnnouncerNotificationTerminatedHandler(),
                                                new TorrentAnnouncerNotificationFailureHandler()
                                        )
                                ),
                                new TorrentFailureHandler()
                        ),
                        new Handlers.Client(
                                new Handlers.Client.Command(
                                        new ClientCommandAddHandler(),
                                        new ClientCommandRemoveHandler(),
                                        new ClientCommandStartHandler(),
                                        new ClientCommandStopHandler()
                                ),
                                new Handlers.Client.Request(
                                        new ClientRequestGetHandler()
                                ),
                                new Handlers.Client.Notification(
                                        new Handlers.Client.Notification.Torrent(
                                                new ClientTorrentNotificationStatusChangedHandler(),
                                                new ClientTorrentNotificationTerminatedHandler(),
                                                new ClientTorrentNotificationFailureHandler()
                                        ),
                                        new Handlers.Client.Notification.Announcer(
                                                new ClientAnnouncerNotificationPeersReceivedHandler(),
                                                new ClientAnnouncerNotificationStatusChangedHandler(),
                                                new ClientAnnouncerNotificationTerminatedHandler(),
                                                new ClientAnnouncerNotificationFailureHandler()
                                        )
                                ),
                                new ClientFailureHandler()
                        )
                )
        );
    }
}
