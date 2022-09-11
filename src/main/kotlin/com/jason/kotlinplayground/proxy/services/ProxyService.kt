package com.jason.kotlinplayground.proxy.services

import com.jason.kotlinplayground.proxy.models.CachedResponse
import com.jason.kotlinplayground.proxy.repositories.CachedResponseRepository
import com.jason.kotlinplayground.proxy.utils.copyHeadersFromRequest
import com.jason.kotlinplayground.proxy.utils.createMapFromRequestHeaders
import com.jason.kotlinplayground.proxy.utils.fetch
import com.jason.kotlinplayground.proxy.utils.toPGObject
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class ProxyService(private val cachedResponseRepository: CachedResponseRepository) {
    suspend fun proxyRequest(urlToProxyTo: String, body: String?, method: HttpMethod, request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String>{
        val result =  fetch(urlToProxyTo, method, copyHeadersFromRequest(request), body).await()
        val resultHeadersPgObject = toPGObject(result.headers)
        val requestHeadersPgObject = toPGObject(createMapFromRequestHeaders(request))
        val cachedResponse = CachedResponse(urlToProxyTo, result.body, resultHeadersPgObject, body, requestHeadersPgObject)
        cachedResponseRepository.save(cachedResponse)
        return result
    }
}


