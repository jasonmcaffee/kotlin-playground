package com.jason.kotlinplayground.proxy.factories

import com.jason.kotlinplayground.proxy.models.db.CachedResponseEntity
import com.jason.kotlinplayground.proxy.models.http.CachedResponse
import com.jason.kotlinplayground.proxy.utils.*
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import javax.servlet.http.HttpServletRequest

fun cachedResponseFromCachedResponseEntity(cachedResponseEntity: CachedResponseEntity): CachedResponse =
    CachedResponse(
        cachedResponseEntity.url,
        cachedResponseEntity.responseStatusCode,
        cachedResponseEntity.responseBody,
        fromJson(cachedResponseEntity.responseHeaders?.value),
        cachedResponseEntity.requestMethod,
        cachedResponseEntity.requestBody,
        fromJson(cachedResponseEntity.requestHeaders.value)!!,
        cachedResponseEntity.id ?: 0
    )

fun cachedResponsesFromCachedResponseEntities(cachedResponseEntities: List<CachedResponseEntity>) :List<CachedResponse> =
    cachedResponseEntities.map { cachedResponseFromCachedResponseEntity(it) }

fun createCachedResponseEntity(urlToProxyTo: String, method: HttpMethod, request: HttpServletRequest, body: String?, result: ResponseEntity<String>) : CachedResponseEntity {
    val resultHeadersPgObject = toPGObject(result.headers)
    val requestHeadersPgObject = toPGObject(createMapFromRequestHeaders(request))
    return CachedResponseEntity(urlToProxyTo, result.statusCode.value(), result.body, resultHeadersPgObject, method.toString(), body, requestHeadersPgObject)
}

fun createResponseEntityFromCachedResponseEntity(cachedResponseEntity: CachedResponseEntity): ResponseEntity<String> {
    val headers = httpHeadersFromPGobject(cachedResponseEntity.responseHeaders)
//    val headers = fromPGobject<HttpHeaders?>(cachedResponseEntity.responseHeaders) //class com.google.gson.internal.LinkedTreeMap cannot be cast to class org.springframework.http.HttpHeaders
    val httpStatus = HttpStatus.valueOf(cachedResponseEntity.responseStatusCode)
    return ResponseEntity.status(httpStatus)
        .headers(headers)
        .body(cachedResponseEntity.responseBody)
}
