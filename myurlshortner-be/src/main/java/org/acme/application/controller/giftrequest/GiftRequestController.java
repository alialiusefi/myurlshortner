package org.acme.application.controller.giftrequest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.acme.application.controller.error.ErrorResponse;
import org.acme.application.usecases.GiftRequestUseCases;
import org.acme.domain.exceptions.giftrequest.CreateGiftRequestError;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.acme.application.controller.Constants.USER_ID_HEADER_KEY;

@Path("/")
public class GiftRequestController {
    private final GiftRequestUseCases useCases;

    public GiftRequestController(GiftRequestUseCases useCases) {
        this.useCases = useCases;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("/shortened-urls/{uniqueIdentifier}/gift-requests/awaiting")
    public Response getAwaitingGiftRequest(
            @HeaderParam(USER_ID_HEADER_KEY) String userId,
            @PathParam("uniqueIdentifier") String uniqueIdentifier
    ) {
        return useCases.getAwaitingGiftRequest(userId, uniqueIdentifier).fold(
                (it) -> {
                    if (it.notFound().isPresent()) {
                        return Response.status(404).entity(ErrorResponse.buildFromDomainError(it.notFound().get())).build();
                    }
                    return Response.status(400).entity(ErrorResponse.buildFromDomainErrors(it.validationError())).build();
                },
                (it) -> Response.ok(new GetAwaitingGiftRequestResponse(it.getId(), it.getUpdatedAt())).build()
        );
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("/shortened-urls/{uniqueIdentifier}/gift-requests")
    public Response createAGiftRequest(
            @HeaderParam(USER_ID_HEADER_KEY) String userId,
            @PathParam("uniqueIdentifier") String uniqueIdentifier,
            CreateGiftRequestRequest request
    ) {
        return useCases.createGiftRequest(request, uniqueIdentifier, userId).fold(
                () -> Response.status(Response.Status.CREATED).build(),
                error -> {
                    if (error.shortenedUrlNotFound().isPresent()) {
                        return Response.status(404).entity(ErrorResponse.buildFromDomainError(error.shortenedUrlNotFound().get())).build();
                    }
                    if (error.opError().isPresent()) {
                        switch (error.opError().get()) {
                            case CreateGiftRequestError.GiftRequestTargetUserCannotBeTheSourceUser a -> {
                                return Response.status(400).entity(ErrorResponse.buildFromDomainError(a)).build();
                            }
                            case CreateGiftRequestError.ShortenedUrlAlreadyHasAGiftRequest a -> {
                                return Response.status(400).entity(ErrorResponse.buildFromDomainError(a)).build();
                            }
                            case CreateGiftRequestError.TargetUserAlreadyHasSuchGiftRequest a -> {
                                return Response.status(409).entity(ErrorResponse.buildFromDomainError(a)).build();
                            }
                            default -> throw new IllegalStateException("Unexpected value: " + error.opError().get());
                        }

                    }
                    return Response.status(400).entity(ErrorResponse.buildFromDomainErrors(error.validationErrors())).build();
                }
        );
    }

    @PUT
    @Produces(APPLICATION_JSON)
    @Path("/gift-requests/awaiting/{id}/cancel")
    public Response cancelAwaitingGiftRequest(
            @HeaderParam(USER_ID_HEADER_KEY) String userId,
            CancelAwaitingGiftRequestRequest request,
            @PathParam("id") String id
    ) {
        return useCases.cancelGiftRequest(
                request,
                id,
                userId
        ).fold(
                () -> Response.status(204).build(),
                (it) -> {
                    if (it.notFound().isPresent()) {
                        return Response.status(404).entity(ErrorResponse.buildFromDomainError(it.notFound().get())).build();
                    } else if (it.wasUpdatedError().isPresent()) {
                        return Response.status(409).entity(ErrorResponse.buildFromDomainError(it.wasUpdatedError().get())).build();
                    }
                    return Response.status(400).entity(ErrorResponse.buildFromDomainErrors(it.validationErrors())).build();
                }
        );
    }

    @PUT
    @Produces(APPLICATION_JSON)
    @Path("/gift-requests/awaiting/{id}/accept")
    public Response acceptAwaitingGiftRequest(
            @HeaderParam(USER_ID_HEADER_KEY) String userId,
            AcceptAwaitingGiftRequest request,
            @PathParam("id") String id
    ) {
        return useCases.acceptGiftRequest(request, userId, id).fold(
                () -> Response.noContent().build(),
                (it) -> {
                    if (!it.notFoundError().isEmpty()) {
                        return Response.status(404).entity(ErrorResponse.buildFromDomainError(it.notFoundError().get())).build();
                    } else if (!it.wasUpdatedError().isEmpty()) {
                        return Response.status(409).entity(ErrorResponse.buildFromDomainError(it.wasUpdatedError().get())).build();
                    }
                    return Response.status(400).entity(ErrorResponse.buildFromDomainErrors(it.validationErrors())).build();
                }
        );
    }
}
