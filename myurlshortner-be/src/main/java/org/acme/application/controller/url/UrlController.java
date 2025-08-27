package org.acme.application.controller.url;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.acme.application.usecases.url.UrlUseCases;

@Path("/urls")
public class UrlController {
    private final UrlUseCases useCases;

    private UrlController(UrlUseCases useCases) {
        this.useCases = useCases;
    }


    @GET
    @Path("/{uniqueIdentifier}")
    public Response getUrl(
            @PathParam("uniqueIdentifier") String uniqueIdentifier,
            @HeaderParam("User-Agent") String userAgent
    ) {
        return useCases.getUrl(uniqueIdentifier, userAgent).fold(
                errors -> Response.status(Response.Status.BAD_REQUEST)
                        .entity(null)
                        .build()
                ,
                success -> Response.status(Response.Status.TEMPORARY_REDIRECT)
                        .location(success)
                        .build()
        );
    }
}
