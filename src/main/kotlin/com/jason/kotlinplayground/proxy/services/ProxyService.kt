package com.jason.kotlinplayground.proxy.services

import com.jason.kotlinplayground.proxy.factories.createCachedResponseEntity
import com.jason.kotlinplayground.proxy.factories.createResponseEntityFromCachedResponseEntity
import com.jason.kotlinplayground.proxy.repositories.CachedResponseRepository
import com.jason.kotlinplayground.proxy.utils.*
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class ProxyService(private val cachedResponseRepository: CachedResponseRepository) {
    suspend fun proxyRequest(urlToProxyTo: String, body: String?, method: HttpMethod, request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String>{
        //first check the db to see if we've already stored a response for the url and requestBody
        val cachedResponseEntity = io { cachedResponseRepository.findCachedResponseBy(method.toString(), urlToProxyTo, body) }
        if(cachedResponseEntity != null) return createResponseEntityFromCachedResponseEntity(cachedResponseEntity)

        //otherwise make the request and save it to the db.
        val result = fetch(urlToProxyTo, method, copyHeadersFromRequest(request), body).await()
        val cachedResponse = createCachedResponseEntity(urlToProxyTo, method, request, body, result)
        io { cachedResponseRepository.save(cachedResponse) }

        return result
    }
}


