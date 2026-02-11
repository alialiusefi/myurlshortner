package com.acme.myurlshortner.consumer.application.client

import io.netty.channel.ConnectTimeoutException
import io.netty.handler.timeout.ReadTimeoutException
import jakarta.ws.rs.core.HttpHeaders
import org.slf4j.LoggerFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.net.SocketTimeoutException
import java.net.URI
import java.time.Duration
import java.time.temporal.ChronoUnit

@Component
class ExternalWebApiClient {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val requestFactory = SimpleClientHttpRequestFactory().apply {
        setReadTimeout(Duration.of(5, ChronoUnit.SECONDS))
        setConnectTimeout(Duration.of(5, ChronoUnit.SECONDS))
    }
    private val restClient = RestClient.builder()
        .requestFactory(requestFactory)
        .build()
    private val TEN_MB = 1024 * 1024 * 10
    private val USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    fun callAndReturnHtmlBody(uri: URI): Result<String> = try {
        restClient
            .get()
            .uri(uri)
            .headers { headers ->
                headers[HttpHeaders.USER_AGENT] = listOf(USER_AGENT)
            }
            .exchange { _, response ->
                logger.info("Status: ${response.statusCode} Target URL: $uri")
                when {
                    response.statusCode.is4xxClientError -> Result.failure(ClientException(response.statusCode.value()))
                    response.statusCode.is5xxServerError -> Result.failure(ServerException(response.statusCode.value()))
                    else -> {
                        val contentType = response.headers.get(HttpHeaders.CONTENT_TYPE) ?: emptyList<String>()
                        if (contentType.none { it.contains("text/html") }) {
                            return@exchange Result.failure(InvalidMediaType())
                        }
                        return@exchange Result.success(response.body.readNBytes(TEN_MB).decodeToString())
                    }
                }
            }!!
    } catch (timeoutException: SocketTimeoutException) {
        Result.failure(TimeoutException(timeoutException))
    }
}