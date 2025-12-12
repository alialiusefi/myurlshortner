package org.acme.application.controller.notification;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.acme.application.controller.error.ErrorResponse;
import org.acme.application.usecases.NotificationUseCases;

import java.util.List;

import static org.acme.application.controller.Constants.USER_ID_HEADER_KEY;

@Path("/notifications")
public class NotificationController {

    private final NotificationUseCases useCases;

    private NotificationController(NotificationUseCases useCases) {
        this.useCases = useCases;
    }

    @GET
    public Response getNotifications(@HeaderParam(USER_ID_HEADER_KEY) String userId) {
        return useCases.getLatestNotifications(userId).fold(
                error -> Response.status(400)
                        .entity(ErrorResponse.buildFromDomainErrors(List.of(error.validationError())))
                        .build(),
                success -> Response.ok(new NotificationResponse(
                        success.stream().map(a ->
                                new NotificationResponse.NotificationRowResponse(
                                        a.id(),
                                        a.type(),
                                        a.params(),
                                        a.readAt()
                                )
                        ).toList()
                )).build()
        );
    }

    @PUT
    @Path("/{id}")
    public Response readNotification(@HeaderParam(USER_ID_HEADER_KEY) String userId, @PathParam("id") String id) {
        return useCases.readNotification(userId, id).fold(
                () -> Response.ok().build(),
                errors -> errors.notFound().map((e) -> Response.status(404).build()).orElseGet(
                        () -> Response.status(400).entity(ErrorResponse.buildFromDomainErrors(errors.validationException())).build()
                )
        );
    }
}
