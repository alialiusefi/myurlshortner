package com.acme.myurlshortner.consumer.application.client

sealed class RestClientException(message: String, throwable: Throwable?) : Throwable(message, throwable) {
    constructor(throwable: Throwable) : this(throwable.message ?: "Error", throwable)
    constructor(message: String) : this(message, null)
}

class ClientException(code: Int) :
    RestClientException("Client returned error code = $code")

class ServerException(code: Int) : RestClientException("Server returned error: $code")

class InvalidMediaType : RestClientException("Server returned response with invalid media type.")

class TimeoutException(timeoutException: Throwable) : RestClientException(timeoutException)
