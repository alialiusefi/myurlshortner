package org.acme.application.controller.url;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.acme.application.controller.error.ErrorResponse;
import org.acme.application.usecases.ShortenedUrlUseCases;
import org.acme.domain.events.V1UserCreatedShortenedUrlEvent;
import org.acme.domain.events.V1UserUpdatedOriginalUrlEvent;
import org.acme.domain.exceptions.url.UpdateOriginalUrlException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.acme.application.controller.Constants.USER_ID_HEADER_KEY;

@Path("/")
public class UrlShortnerController {
    @ConfigProperty(name = "app.hostname")
    private String hostname;
    private final ShortenedUrlUseCases shortenedUrlUseCases;

    public UrlShortnerController(
            ShortenedUrlUseCases shortenedUrlUseCases
    ) {
        this.shortenedUrlUseCases = shortenedUrlUseCases;
    }

    @POST
    @Path("/unique-identifiers")
    @Produces(APPLICATION_JSON)
    public Response generateUniqueIdentifier() {
        return Response.status(201).entity(new GenerateUniqueIdentifierResponse(this.shortenedUrlUseCases.generateUniqueIdentifier())).build();
    }

    @POST
    @Path("/shorten")
    @Produces(APPLICATION_JSON)
    public Response createShortenedUrl(
            ShortenUrlRequest request,
            @DefaultValue("1")
            @HeaderParam(USER_ID_HEADER_KEY) String userIdHeader
    ) {
        return this.shortenedUrlUseCases.createShortenedUrl(userIdHeader, request).fold(
                error -> Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                error.opError().map(a -> ErrorResponse.buildFromDomainErrors(List.of(a)))
                                        .orElseGet(() -> ErrorResponse.buildFromDomainErrors(error.validationExceptions()))

                        ).build(),
                success -> Response.status(Response.Status.CREATED)
                        .entity(new ShortenUrlResponse(success.shortenedUrl(hostname))).build()
        );
    }

    @GET
    @Path("/shortened-urls")
    @Produces(APPLICATION_JSON)
    public Response getShortenedUrls(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("order") String order,
            @DefaultValue("1")
            @HeaderParam(USER_ID_HEADER_KEY) String userIdHeader
    ) {
        return this.shortenedUrlUseCases.listAvailableUrls(
                page,
                size,
                order,
                userIdHeader
        ).fold(
                error -> Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.buildFromApplicationErrors(error.getErrors())).build(),
                success -> {
                    var total = success._1;
                    var results = success._2.stream()
                            .map(
                                    row -> new UrlList.UrlRow(
                                            row.originalUrl().toString(),
                                            row.shortenedUrl(hostname),
                                            row.accessCount(),
                                            row.createdAt(),
                                            row.isEnabled()
                                    )
                            ).toList();
                    return Response.ok().entity(new UrlList(results, total)).build();
                }
        );
    }

    @PATCH
    @Path("/shortened-urls/{uniqueIdentifier}")
    @Produces(APPLICATION_JSON)
    public Response updateOriginalUrl(
            UpdateOriginalUrlRequest request,
            @PathParam("uniqueIdentifier") String uniqueIdentifier,
            @DefaultValue("1")
            @HeaderParam(USER_ID_HEADER_KEY) String userIdHeader
    ) {
        if (request.isEnabled() == null) {
            request = new UpdateOriginalUrlRequest(request.url(), true);
        }
        return shortenedUrlUseCases.updateOriginalUrl(uniqueIdentifier, request, userIdHeader).fold(
                fail -> {
                    return fail.operationError().map(
                            error -> switch (error) {
                                case UpdateOriginalUrlException.ShortenedUrlIsNotFound notFound ->
                                        Response.status(Response.Status.NOT_FOUND).build();

                                default -> throw new IllegalStateException("Unexpected value: " + error);
                            }
                    ).orElseGet(() ->
                            Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.buildFromDomainErrors(
                                            fail.validationErrors()
                                    )
                            ).build());
                },
                success -> Response.status(Response.Status.NO_CONTENT).build()
        );
    }

    @GET
    @Path("/shortened-urls/{uniqueIdentifier}")
    public Response shortenedUrl(
            @PathParam("uniqueIdentifier") String uniqueIdentifier,
            @DefaultValue("1")
            @HeaderParam(USER_ID_HEADER_KEY) String userIdHeader
    ) {
        return shortenedUrlUseCases.getShortenedUrl(userIdHeader, uniqueIdentifier).fold(
                fail -> fail.notFound.map(a -> Response.status(404).build()).orElseGet(
                        () -> Response.status(400).entity(ErrorResponse.buildFromDomainErrors(fail.validationException)).build()),
                success -> Response.ok(new ShortenedUrlResponse(
                        success.getPublicIdentifier(),
                        success.shortenedUrl(hostname),
                        success.getCreatedAt(),
                        success.getUpdatedAt(),
                        success.getOriginalUrl().toString(),
                        success.isEnabled())).build()
        );
    }

    @GET
    @Path("/shortened-urls/{uniqueIdentifier}/history")
    @Produces(APPLICATION_JSON)
    public Response shortenedUrlHistory(
            @PathParam("uniqueIdentifier") String uniqueIdentifier,
            @QueryParam("size") Integer size,
            @QueryParam("offset") Integer offset,
            @QueryParam("from") String from,
            @DefaultValue("1")
            @HeaderParam(USER_ID_HEADER_KEY) String userIdHeader
    ) {
        return shortenedUrlUseCases.getShortenedUrlHistory(userIdHeader, uniqueIdentifier, offset, size, from).fold(
                fail ->
                        fail.error.map(notFound -> Response.status(404).build())
                                .orElseGet(() -> Response.status(400).entity(ErrorResponse.buildFromApplicationErrors(fail.errors)).build())
                ,
                events ->
                        Response.ok(new ShortenedUrlHistoryResponse(events.stream().map(e ->
                                switch (e) {
                                    case V1UserCreatedShortenedUrlEvent event ->
                                            new ShortenedUrlHistoryResponse.ShortenedUrlHistoryRow(
                                                    event.originalUrl().toString(),
                                                    event.createdAt()
                                            );
                                    case V1UserUpdatedOriginalUrlEvent event ->
                                            new ShortenedUrlHistoryResponse.ShortenedUrlHistoryRow(
                                                    event.newOriginalUrl().toString(),
                                                    event.updatedAt()
                                            );
                                }).toList())
                        ).build()
        );
    }
}
