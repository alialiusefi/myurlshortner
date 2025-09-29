package org.acme.application.controller.url;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.acme.application.controller.error.ErrorResponse;
import org.acme.application.usecases.ShortenedUrlUseCases;
import org.acme.domain.exceptions.url.UpdateOriginalUrlException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

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
    @Path("/shorten")
    @Produces(APPLICATION_JSON)
    public Response generateAShortenedUrl(
            ShortenUrlRequest request
    ) {
        return this.shortenedUrlUseCases.generateShortenedUrl(request.url()).fold(
                error -> Response.status(Response.Status.BAD_REQUEST)
                        .entity(ErrorResponse.buildFromDomainErrors(error.errors())).build(),
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
            @QueryParam("order") String order
    ) {
        return this.shortenedUrlUseCases.listAvailableUrls(
                page,
                size,
                order
        ).fold(
                error -> Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.buildFromApplicationErrors(error.getErrors())).build(),
                success -> {
                    var total = success._1;
                    var results = success._2.stream()
                            .map(
                                    row -> new UrlList.UrlRow(
                                            row.shortenedUrl().getOriginalUrl().toString(),
                                            row.shortenedUrl().shortenedUrl(hostname),
                                            row.accessCount(),
                                            row.shortenedUrl().getCreatedAt(),
                                            row.shortenedUrl().isEnabled()
                                    )
                            ).toList();
                    return Response.ok().entity(new UrlList(results, total)).build();
                }
        );
    }

    @PATCH
    @Path("/shortened-urls/{uniqueIdentifier}")
    @Produces(APPLICATION_JSON)
    public Response updateOriginalUrl(UpdateOriginalUrlRequest request, @PathParam("uniqueIdentifier") String uniqueIdentifier) {
        if (request.isEnabled() == null) {
            request = new UpdateOriginalUrlRequest(request.url(), true);
        }
        return shortenedUrlUseCases.updateOriginalUrl(uniqueIdentifier, request).fold(
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
}
