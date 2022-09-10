package com.jason.kotlinplayground.proxy.utils

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

fun fetch(url: String, method: HttpMethod, headers: HttpHeaders, body: String? = null): ResponseEntity<String> {
    val uri = URI(url)
    val httpEntity = HttpEntity(body, headers)
    val factory = BufferingClientHttpRequestFactory(SimpleClientHttpRequestFactory())
    val restTemplate = RestTemplate(factory)
    return try {
        restTemplate.exchange(uri, method, httpEntity, String::class.java)
    }catch(e: HttpStatusCodeException){
        ResponseEntity.status(e.rawStatusCode)
            .headers(e.responseHeaders)
            .body(e.responseBodyAsString)
    }
}
