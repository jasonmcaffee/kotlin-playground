package com.jason.kotlinplayground.proxy.services

import com.jason.kotlinplayground.proxy.models.CachedResponse
import com.jason.kotlinplayground.proxy.repositories.CachedResponseRepository
import com.jason.kotlinplayground.proxy.utils.*
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class ProxyService(private val cachedResponseRepository: CachedResponseRepository) {
    suspend fun proxyRequest(urlToProxyTo: String, body: String?, method: HttpMethod, request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String>{
        //first check the db to see if we've already stored a response for the url and requestBody
        val dbCachedResponse = io { cachedResponseRepository.findCachedResponseBy(method.toString(), urlToProxyTo, body) }
        if(dbCachedResponse != null) return createResponseEntityFromCachedResponse(dbCachedResponse)

        //otherwise make the request and save it to the db.
        val result = fetch(urlToProxyTo, method, copyHeadersFromRequest(request), body).await()
        val cachedResponse = createCachedResponse(urlToProxyTo, method, request, body, result)
        io { cachedResponseRepository.save(cachedResponse) }
        return result
    }
}

fun createCachedResponse(urlToProxyTo: String, method: HttpMethod, request: HttpServletRequest, body: String?, result: ResponseEntity<String>) : CachedResponse{
    val resultHeadersPgObject = toPGObject(result.headers)
    val requestHeadersPgObject = toPGObject(createMapFromRequestHeaders(request))

    return CachedResponse(urlToProxyTo, result.statusCode.value(), result.body, resultHeadersPgObject, method.toString(), body, requestHeadersPgObject)
}

fun createResponseEntityFromCachedResponse(cachedResponse: CachedResponse): ResponseEntity<String>{
    val headers = httpHeadersFromPGobject(cachedResponse.responseHeaders)
    val httpStatus = HttpStatus.valueOf(cachedResponse.responseStatusCode)
    return ResponseEntity.status(httpStatus)
        .headers(headers)
        .body(cachedResponse.responseBody)
}

