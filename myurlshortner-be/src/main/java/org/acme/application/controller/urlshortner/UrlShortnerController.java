package org.acme.application.controller.urlshortner;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.acme.application.usecases.ShortenedUrlUseCases;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
public class UrlShortnerController {
    @ConfigProperty(name = "app.hostname")
    private String hostname;
    private final ShortenedUrlUseCases useCases;

    public UrlShortnerController(ShortenedUrlUseCases useCases) {
        this.useCases = useCases;
    }

    private final String MOCK_URL = "https://www.shortner.com/goto/wLf16-ft";

    @GET
    @Path("/shortened-urls")
    @Produces(APPLICATION_JSON)
    public Response getShortenedUrls(
            @QueryParam("page") String page,
            @QueryParam("size") String size,
            @QueryParam("status") String status
    ) {
        var row = new UrlShortnerList.UrlShortnerListRow(
                "https://www.google.com",
                MOCK_URL
        );
        var total = 1L;
        return Response.ok(
                new UrlShortnerList(
                        List.of(row),
                        total
                )
        ).build();
    }

    @POST
    @Path("/shorten")
    @Produces(APPLICATION_JSON)
    public Response generateAShortenedUrl(
            ShortenUrlRequest request
    ) {
        return this.useCases.generateShortenedUrl(request.url()).fold(
                errors -> {
                    var response = new ShortenUrlErrorsResponse(
                            errors.getErrors().stream().map(
                                    err -> new ShortenUrlError(err.getCode(), err.getMessage())
                            ).toList()
                    );
                    return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
                },
                success -> Response.status(Response.Status.CREATED)
                        .entity(new ShortenUrlResponse(success.shortenedUrl(hostname))).build()
        );
    }
}
