package com.github.jurisliepins.resources;

import com.github.jurisliepins.BitTorrentClient;
import com.github.jurisliepins.client.message.ClientCommandResult;
import com.github.jurisliepins.client.message.ClientResponse;
import com.github.jurisliepins.definitions.response.Result;
import com.github.jurisliepins.info.InfoHash;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;

import java.io.IOException;

@Path("/torrent")
public class TorrentResource {

    @Inject
    BitTorrentClient bitTorrentClient;

    @GET
    @Path("/{infoHash}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result<ClientResponse> get(final @PathParam("infoHash") InfoHash infoHash) {
        Log.info("Getting torrent '%s'".formatted(infoHash));
        return Result.success(bitTorrentClient.get(infoHash));
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Result<ClientCommandResult> add(final @MultipartForm MultipartInput input) throws IOException {
        Log.info("Adding torrent from torrent file '%s'"
                         .formatted(input.getParts()
                                            .getFirst()
                                            .getFileName()));
        var bytes = input.getParts()
                .getFirst()
                .getBody()
                .readAllBytes();
        return Result.success(bitTorrentClient.add(bytes));
    }

    @POST
    @Path("/remove/{infoHash}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result<ClientCommandResult> remove(final @PathParam("infoHash") InfoHash infoHash) {
        Log.info("Removing torrent '%s'".formatted(infoHash));
        return Result.success(bitTorrentClient.remove(infoHash));
    }

    @POST
    @Path("/start/{infoHash}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result<ClientCommandResult> start(final @PathParam("infoHash") InfoHash infoHash) {
        Log.info("Starting torrent '%s'".formatted(infoHash));
        return Result.success(bitTorrentClient.start(infoHash));
    }

    @POST
    @Path("/stop/{infoHash}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result<ClientCommandResult> stop(final @PathParam("infoHash") InfoHash infoHash) {
        Log.info("Stopping torrent '%s'".formatted(infoHash));
        return Result.success(bitTorrentClient.stop(infoHash));
    }
}
