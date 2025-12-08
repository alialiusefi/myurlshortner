package com.acme.myurlshortner.consumer.application.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.jboss.logging.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestClient
import java.net.URI

// https://spring.io/blog/2025/09/30/the-state-of-http-clients-in-spring
@Component
class ShortenedUrlApiClient(
    @Value($$"${app.be.baseurl}")
    private val baseUrl: String,
    @Value($$"${app.be.apikey}")
    private val apiKey: String
) {
    private val mapper = ObjectMapper()
    private val logger = Logger.getLogger(this::class.java)
    private val errorHandler = object : ResponseErrorHandler {
        override fun hasError(response: ClientHttpResponse): Boolean = response.statusCode.value() >= 400
        override fun handleError(url: URI, method: HttpMethod, response: ClientHttpResponse) {
            logger.error("Unexpected upstream error json=${mapper.readTree(response.body.readAllBytes())}")
            throw RuntimeException("Unexpected upstream error.")
        }

    }
    private val restClient = RestClient.builder().baseUrl(baseUrl).defaultStatusHandler(errorHandler).build()
    private val API_KEY_HEADER = "Api-Key"

    data class GetShortenedUrlByIdResponse(
        val user_id: Long
    )

    suspend fun getShortenedUrlById(uniqueIdentifier: String): GetShortenedUrlByIdResponse =
        restClient.get().uri("/shortened-urls/{id}", uniqueIdentifier)
            .header(API_KEY_HEADER, apiKey)
            .retrieve()
            .body(GetShortenedUrlByIdResponse::class.java)!!

}
