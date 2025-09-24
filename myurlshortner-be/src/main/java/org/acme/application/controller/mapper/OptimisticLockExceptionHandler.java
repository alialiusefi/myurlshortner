package org.acme.application.controller.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.acme.application.controller.error.ErrorResponse;
import org.acme.application.repo.exception.ShortenedUrlOptimisticLockException;

import java.util.List;

@Provider
public class OptimisticLockExceptionHandler implements ExceptionMapper<ShortenedUrlOptimisticLockException> {
    @Override
    public Response toResponse(ShortenedUrlOptimisticLockException exception) {
        return Response.status(Response.Status.CONFLICT).entity(
                new ErrorResponse(List.of(new ErrorResponse.Error(
                        "SHORTENED_URL_UPDATE_CONFLICT",
                        "The existing shortened url was updated already, please retry your operation."
                )))
        ).build();
    }
}
