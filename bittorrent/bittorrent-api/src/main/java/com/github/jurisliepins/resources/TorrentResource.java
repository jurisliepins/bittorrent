package com.github.jurisliepins.resources;

import com.github.jurisliepins.client.ClientCommandResult;
import com.github.jurisliepins.client.ClientResponse;
import com.github.jurisliepins.definitions.response.Result;
import com.github.jurisliepins.info.InfoHash;
import com.github.jurisliepins.services.TorrentService;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/torrent")
public class TorrentResource {

    @Inject
    TorrentService torrentService;

    @GET
    @Path("/{infoHash}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result<ClientResponse> get(final InfoHash infoHash) {
        Log.info("Getting torrent '%s'".formatted(infoHash));
        return Result.success(torrentService.get(infoHash));
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Result<ClientCommandResult> add(final Object torrent) {
        Log.info("Adding torrent '%s'".formatted(torrent));
        return Result.success(torrentService.add(torrent));
    }

    @POST
    @Path("/{infoHash}/remove")
    @Produces(MediaType.APPLICATION_JSON)
    public Result<ClientCommandResult> remove(final InfoHash infoHash) {
        Log.debug("Removing torrent '%s'".formatted(infoHash));
        return Result.success(torrentService.remove(infoHash));
    }

    @POST
    @Path("/{infoHash}/start")
    @Produces(MediaType.APPLICATION_JSON)
    public Result<ClientCommandResult> start(final InfoHash infoHash) {
        Log.info("Starting torrent '%s'".formatted(infoHash));
        return Result.success(torrentService.start(infoHash));
    }

    @POST
    @Path("/{infoHash}/stop")
    @Produces(MediaType.APPLICATION_JSON)
    public Result<ClientCommandResult> stop(final InfoHash infoHash) {
        Log.info("Stopping torrent '%s'".formatted(infoHash));
        return Result.success(torrentService.stop(infoHash));
    }
}
