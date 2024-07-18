package com.github.jurisliepins.resources;

import com.github.jurisliepins.BitTorrentClient;
import com.github.jurisliepins.client.ClientCommandResult;
import com.github.jurisliepins.client.ClientResponse;
import com.github.jurisliepins.definitions.response.Result;
import com.github.jurisliepins.info.InfoHash;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;

import java.util.List;
import java.util.stream.Collectors;

@Path("/torrent")
public class TorrentResource {

    @Inject
    BitTorrentClient bitTorrentClient;

    @GET
    @Path("/{infoHash}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result<ClientResponse> get(final InfoHash infoHash) {
        Log.info("Getting torrent '%s'".formatted(infoHash));
        return Result.success(bitTorrentClient.get(infoHash));
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Result<List<ClientCommandResult>> add(final @MultipartForm MultipartInput input) {
        final List<ClientCommandResult> results = input.getParts()
                .stream()
                .map(part -> {
                    Log.info("Adding torrent from meta-info file '%s'".formatted(part.getFileName()));
                    try {
                        return bitTorrentClient.add(part.getBody().readAllBytes());
                    } catch (Exception e) {
                        Log.error("Failed to add torrent '%s'".formatted(part.getFileName()), e);
                        return new ClientCommandResult.Failure(
                                null,
                                "Failed to add torrent '%s' with '%s'".formatted(part.getFileName(), e.getMessage()));
                    }
                })
                .collect(Collectors.toList());
        return Result.success(results);
    }

    @POST
    @Path("/remove/{infoHash}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result<ClientCommandResult> remove(final InfoHash infoHash) {
        Log.info("Removing torrent '%s'".formatted(infoHash));
        return Result.success(bitTorrentClient.remove(infoHash));
    }

    @POST
    @Path("/start/{infoHash}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result<ClientCommandResult> start(final InfoHash infoHash) {
        Log.info("Starting torrent '%s'".formatted(infoHash));
        return Result.success(bitTorrentClient.start(infoHash));
    }

    @POST
    @Path("/stop/{infoHash}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result<ClientCommandResult> stop(final InfoHash infoHash) {
        Log.info("Stopping torrent '%s'".formatted(infoHash));
        return Result.success(bitTorrentClient.stop(infoHash));
    }
}
