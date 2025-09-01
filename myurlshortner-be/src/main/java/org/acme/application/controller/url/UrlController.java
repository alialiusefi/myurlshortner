package org.acme.application.controller.url;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.acme.application.controller.error.ErrorResponse;
import org.acme.application.usecases.url.RedirectUserUseCases;
import org.acme.domain.exceptions.url.GetUrlException;

@Path("/urls")
public class UrlController {
    private final RedirectUserUseCases useCases;

    private UrlController(RedirectUserUseCases useCases) {
        this.useCases = useCases;
    }


    @GET
    @Path("/{uniqueIdentifier}")
    public Response getUrl(
            @PathParam("uniqueIdentifier") String uniqueIdentifier,
            @HeaderParam("User-Agent") String userAgent
    ) {
        return useCases.getUrl(uniqueIdentifier, userAgent).fold(
                errors -> errors.operationError().map(
                        op -> switch (op) {
                            case GetUrlException.ShortenedUrlIsNotFound ignored ->
                                    Response.status(Response.Status.NOT_FOUND).build();
                            default -> throw new IllegalStateException("Unexpected value: " + op);
                        }
                ).orElseGet(() ->
                        errors.urlValidationErrors()
                                .map(e ->
                                        Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.buildFromDomainErrors(e)).build()
                                ).orElseThrow()
                ),

                success -> Response.status(Response.Status.TEMPORARY_REDIRECT)
                        .location(success)
                        .build()
        );
    }
}
