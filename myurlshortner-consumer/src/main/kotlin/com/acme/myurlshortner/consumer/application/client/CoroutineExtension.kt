package com.acme.myurlshortner.consumer.application.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend fun <T> executeIOAndAwait(execute: suspend () -> T) = coroutineScope {
    async(Dispatchers.IO) {
        execute()
    }.await()
}
