package com.jason.kotlinplayground.proxy.utils

import kotlinx.coroutines.*
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import java.net.URI
import javax.servlet.http.HttpServletRequest

fun copyHeadersFromRequest(request: HttpServletRequest) = HttpHeaders().also{ headers ->
    request.headerNames.toList().forEach{
        headers.set(it, request.getHeader(it))
    }
    headers.remove(HttpHeaders.ACCEPT_ENCODING)
}

/**
 * https://developer.android.com/kotlin/coroutines/coroutines-adv
 * Dispatchers.IO - This dispatcher is optimized to perform disk or network I/O outside of the main thread. Examples include using the Room component, reading from or writing to files, and running any network operations.
 */
suspend fun fetch(url: String, method: HttpMethod, headers: HttpHeaders, body: String? = null): Deferred<ResponseEntity<String>> = withContext(Dispatchers.IO) {
    async {
        val uri = URI(url)
        val httpEntity = HttpEntity(body, headers)
        val factory = BufferingClientHttpRequestFactory(SimpleClientHttpRequestFactory())
        val restTemplate = RestTemplate(factory)
        try {
            restTemplate.exchange(uri, method, httpEntity, String::class.java)
        }catch(e: HttpStatusCodeException){
            ResponseEntity.status(e.rawStatusCode)
                .headers(e.responseHeaders)
                .body(e.responseBodyAsString)
        }
    }
}

suspend fun fetch(url: String, method: HttpMethod, headers: Map<String, String>, body: String? = null): Deferred<ResponseEntity<String>> =
    fetch(url, method, createHeadersFromMap(headers), body)
fun createHeadersFromMap(headers: Map<String, String>): HttpHeaders{
    val httpHeaders = HttpHeaders()
    headers.entries.forEach{ (key, value) ->
        httpHeaders.set(key, value)
    }
    return httpHeaders
}

//suspend fun <T>await(promise: Deferred<T>): T{
//    return promise.await()
//}