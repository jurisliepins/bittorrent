package com.github.jurisliepins.resources;

import com.github.jurisliepins.BitTorrentClient;
import com.github.jurisliepins.client.ClientCommandResult;
import com.github.jurisliepins.client.ClientResponse;
import com.github.jurisliepins.definitions.request.TorrentRemoveRequest;
import com.github.jurisliepins.definitions.request.TorrentStartRequest;
import com.github.jurisliepins.definitions.request.TorrentStopRequest;
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
                        return new ClientCommandResult.Failure(
                                null,
                                "Failed to add torrent '%s' with '%s'".formatted(part.getFileName(), e.getMessage()));
                    }
                })
                .collect(Collectors.toList());
        return Result.success(results);
    }

    @POST
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Result<List<ClientCommandResult>> remove(final TorrentRemoveRequest request) {
        final List<ClientCommandResult> results = request.infoHashes()
                .stream()
                .map(infoHash -> {
                    Log.info("Removing torrent '%s'".formatted(infoHash));
                    return bitTorrentClient.remove(infoHash);
                })
                .collect(Collectors.toList());
        return Result.success(results);
    }

    @POST
    @Path("/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Result<List<ClientCommandResult>> start(final TorrentStartRequest request) {
        final List<ClientCommandResult> results = request.infoHashes()
                .stream()
                .map(infoHash -> {
                    Log.info("Starting torrent '%s'".formatted(infoHash));
                    return bitTorrentClient.start(infoHash);
                })
                .collect(Collectors.toList());
        return Result.success(results);
    }

    @POST
    @Path("/stop")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Result<List<ClientCommandResult>> stop(final TorrentStopRequest request) {
        final List<ClientCommandResult> results = request.infoHashes()
                .stream()
                .map(infoHash -> {
                    Log.info("Stopping torrent '%s'".formatted(infoHash));
                    return bitTorrentClient.stop(infoHash);
                })
                .collect(Collectors.toList());
        return Result.success(results);
    }
}
