package com.jason.kotlinplayground.proxy.services

import com.jason.kotlinplayground.proxy.models.CachedResponse
import com.jason.kotlinplayground.proxy.repositories.CachedResponseRepository
import com.jason.kotlinplayground.proxy.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class ProxyService(private val cachedResponseRepository: CachedResponseRepository) {
    suspend fun proxyRequest(urlToProxyTo: String, body: String?, method: HttpMethod, request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String>{

        val cachedResponses = withContext(Dispatchers.IO) {
            cachedResponseRepository.findCachedResponsesBy(urlToProxyTo, body)
        }
        println(cachedResponses)

        val dbCachedResult = cachedResponses.firstOrNull()

        if(dbCachedResult != null){
            val result = createResponseEntityFromCachedResponse(dbCachedResult)
            return result
        }
        val result = fetch(urlToProxyTo, method, copyHeadersFromRequest(request), body).await()

        val cachedResponse = createCachedResponse(urlToProxyTo, request, body, result)
        withContext(Dispatchers.IO) {
            cachedResponseRepository.save(cachedResponse)
        }
        return result
    }
}

fun createCachedResponse(urlToProxyTo: String, request: HttpServletRequest, body: String?, result: ResponseEntity<String>) : CachedResponse{
    val resultHeadersPgObject = toPGObject(result.headers)
    val requestHeadersPgObject = toPGObject(createMapFromRequestHeaders(request))
    return CachedResponse(urlToProxyTo, result.body, resultHeadersPgObject, body, requestHeadersPgObject)
}

fun createResponseEntityFromCachedResponse(cachedResponse: CachedResponse): ResponseEntity<String>{
    val headers = httpHeadersFromPGobject(cachedResponse.responseHeaders)
    return ResponseEntity.status(HttpStatus.OK)
        .headers(headers)
        .body(cachedResponse.responseBody)
}

