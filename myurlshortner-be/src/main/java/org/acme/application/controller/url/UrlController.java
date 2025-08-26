package org.acme.application.controller.url;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/urls")
public class UrlController {
    private UrlController() {}


    @GET
    @Path("/{uniqueIdentifier}")
    public Response getUrl(@PathParam("uniqueIdentifier") String uniqueIdentifier) {
        //mock
        return Response.status(Response.Status.TEMPORARY_REDIRECT)
                .location(URI.create("https://www.google.com"))
                .build();
    }

}
