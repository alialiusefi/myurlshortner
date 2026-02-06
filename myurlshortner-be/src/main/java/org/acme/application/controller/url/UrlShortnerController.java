package org.acme.application.controller.url;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.acme.application.controller.error.ErrorResponse;
import org.acme.application.usecases.ShortenedUrlUseCases;
import org.acme.application.util.PatchField;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.events.V1UserUpdatedOriginalUrlEvent;
import org.acme.domain.events.V1UserUpdatedTitleEvent;
import org.acme.domain.events.V2UserCreatedShortenedUrlEvent;
import org.acme.domain.events.V2UserGiftedShortenedUrlEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.function.BiFunction;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.acme.application.controller.Constants.API_KEY;
import static org.acme.application.controller.Constants.USER_ID_HEADER_KEY;
import static org.acme.application.controller.url.PatchShortenedUrlRequest.*;

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
    @Path("/unique-identifiers")
    @Produces(APPLICATION_JSON)
    public Response generateUniqueIdentifier() {
        return Response.status(201).entity(new GenerateUniqueIdentifierResponse(this.shortenedUrlUseCases.generateUniqueIdentifier())).build();
    }

    @POST
    @Path("/shorten")
    @Produces(APPLICATION_JSON)
    public Response createShortenedUrl(
            ShortenUrlRequest request,
            @HeaderParam(USER_ID_HEADER_KEY) String userIdHeader
    ) {
        return this.shortenedUrlUseCases.createShortenedUrl(userIdHeader, request).fold(
                error -> Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                error.opError().map(a -> ErrorResponse.buildFromDomainErrors(List.of(a)))
                                        .orElseGet(() -> ErrorResponse.buildFromDomainErrors(error.validationExceptions()))

                        ).build(),
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
            @QueryParam("order") String order,
            @DefaultValue("1")
            @HeaderParam(USER_ID_HEADER_KEY) String userIdHeader
    ) {
        return this.shortenedUrlUseCases.listAvailableUrls(
                page,
                size,
                order,
                userIdHeader
        ).fold(
                error -> Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.buildFromApplicationErrors(error.getErrors())).build(),
                success -> {
                    var total = success._1;
                    var results = success._2.stream()
                            .map(
                                    row -> new UrlList.UrlRow(
                                            row.originalUrl().toString(),
                                            row.shortenedUrl(hostname),
                                            row.accessCount(),
                                            row.createdAt(),
                                            row.isEnabled(),
                                            row.title()
                                    )
                            ).toList();
                    return Response.ok().entity(new UrlList(results, total)).build();
                }
        );
    }

    @PATCH
    @Path("/shortened-urls/{uniqueIdentifier}")
    @Produces(APPLICATION_JSON)
    public Response patchShortenedUrl(
            JsonNode request,
            @PathParam("uniqueIdentifier") String uniqueIdentifier,
            @HeaderParam(USER_ID_HEADER_KEY) String userIdHeader
    ) {
        BiFunction<JsonNode, String, PatchField<String>> parseString = (JsonNode a, String field) -> {
            var isSet = a.has(field);
            if (isSet) {
                if (!a.get(field).isNull()) {
                    return new PatchField<>(a.get(field).asText(), isSet);
                }
                return new PatchField<>(null, isSet);
            } else {
                return new PatchField<>(null, isSet);
            }
        };
        BiFunction<JsonNode, String, PatchField<Boolean>> parseBoolean = (JsonNode a, String field) -> {
            var isSet = a.has(field);
            if (isSet) {
                return new PatchField<>(a.get(field).asBoolean(), isSet);
            } else {
                return new PatchField<>(null, isSet);
            }
        };

        return shortenedUrlUseCases.patchShortenedUrl(
                new PatchShortenedUrlRequest(
                        parseString.apply(request, URL_FIELD),
                        parseBoolean.apply(request, IS_ENABLED_FIELD),
                        parseString.apply(request, TITLE_FIELD)
                ),
                userIdHeader,
                uniqueIdentifier
        ).fold(
                fail -> fail.notFound().map(a -> Response.status(404).build()).orElseGet(
                        () -> Response.status(400).entity(ErrorResponse.buildFromDomainErrors(fail.validationErrors())).build())
                ,
                this::toResponse
        );
    }

    @GET
    @Path("/shortened-urls/{uniqueIdentifier}")
    public Response shortenedUrl(
            @PathParam("uniqueIdentifier") String uniqueIdentifier,
            @HeaderParam(USER_ID_HEADER_KEY) String userIdHeader,
            @HeaderParam(API_KEY) String apiKey
    ) {
        return shortenedUrlUseCases.getShortenedUrl(userIdHeader, uniqueIdentifier, apiKey).fold(
                fail -> fail.notFound.map(a -> Response.status(404).build()).orElseGet(
                        () -> Response.status(400).entity(ErrorResponse.buildFromDomainErrors(fail.validationException)).build()),
                this::toResponse
        );
    }

    private Response toResponse(ShortenedUrl url) {
        return Response.ok(
                new ShortenedUrlResponse(
                        url.getPublicIdentifier(),
                        url.shortenedUrl(hostname),
                        url.getCreatedAt(),
                        url.getUpdatedAt(),
                        url.getOriginalUrl().toString(),
                        url.isEnabled(),
                        url.getUserId(),
                        url.getTitle()
                )
        ).build();
    }


    @GET
    @Path("/shortened-urls/{uniqueIdentifier}/history")
    @Produces(APPLICATION_JSON)
    public Response shortenedUrlHistory(
            @PathParam("uniqueIdentifier") String uniqueIdentifier,
            @QueryParam("size") Integer size,
            @QueryParam("offset") Integer offset,
            @QueryParam("from") String from,
            @DefaultValue("1")
            @HeaderParam(USER_ID_HEADER_KEY) String userIdHeader
    ) {
        return shortenedUrlUseCases.getShortenedUrlHistory(userIdHeader, uniqueIdentifier, offset, size, from).fold(
                fail ->
                        fail.error.map(notFound -> Response.status(404).build())
                                .orElseGet(() -> Response.status(400).entity(ErrorResponse.buildFromApplicationErrors(fail.errors)).build())
                ,
                events ->
                        Response.ok(new ShortenedUrlHistoryResponse(events.stream().map(e ->
                                switch (e) {
                                    case V2UserCreatedShortenedUrlEvent event ->
                                            new ShortenedUrlHistoryResponse.UrlUpdatedHistoryRow(
                                                    event.originalUrl().toString(),
                                                    event.createdAt()
                                            );
                                    case V1UserUpdatedOriginalUrlEvent event ->
                                            new ShortenedUrlHistoryResponse.UrlUpdatedHistoryRow(
                                                    event.newOriginalUrl().toString(),
                                                    event.updatedAt()
                                            );
                                    case V2UserGiftedShortenedUrlEvent event ->
                                            new ShortenedUrlHistoryResponse.UrlUpdatedHistoryRow(
                                                    event.originalUrl().toString(),
                                                    event.createdAt()
                                            );
                                    case V1UserUpdatedTitleEvent event ->
                                            new ShortenedUrlHistoryResponse.TitleUpdatedHistoryRow(
                                                    event.newTitle(),
                                                    event.createdAt()
                                            );
                                }).toList())
                        ).build()
        );
    }
}
