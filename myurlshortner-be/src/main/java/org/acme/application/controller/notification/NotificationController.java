package org.acme.application.controller.notification;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
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
                                        a.params()
                                )
                        ).toList()
                )).build()
        );
    }
}
