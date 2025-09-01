package org.acme.application.controller.url;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.acme.application.controller.error.ErrorResponse;
import org.acme.application.usecases.ShortenedUrlUseCases;
import org.acme.application.usecases.url.UrlUseCases;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
public class UrlShortnerController {
    @ConfigProperty(name = "app.hostname")
    private String hostname;
    private final ShortenedUrlUseCases shortenedUrlUseCases;
    private final UrlUseCases urlUseCases;

    public UrlShortnerController(
            ShortenedUrlUseCases shortenedUrlUseCases,
            UrlUseCases urlUseCases
    ) {
        this.shortenedUrlUseCases = shortenedUrlUseCases;
        this.urlUseCases = urlUseCases;
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
            @QueryParam("size") Integer size
    ) {
        return this.urlUseCases.listAvailableUrls(
                page,
                size
        ).fold(
                error -> Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.buildFromDomainApplicationErrors(error.getErrors())).build(),
                success -> {
                    var total = success._1;
                    var results = success._2.stream()
                            .map(
                                    row -> new UrlList.UrlRow(
                                            row.getOriginalUrl().toString(),
                                            row.shortenedUrl(hostname))
                            ).toList();
                    return Response.ok().entity(new UrlList(results, total)).build();
                }
        );
    }
}
