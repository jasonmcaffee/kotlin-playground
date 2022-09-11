package com.jason.kotlinplayground.proxy.services

import com.jason.kotlinplayground.proxy.repositories.CachedResponseRepository
import com.jason.kotlinplayground.proxy.utils.copyHeadersFromRequest
import com.jason.kotlinplayground.proxy.utils.fetch
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class ProxyService(private val cachedResponseRepository: CachedResponseRepository) {
    suspend fun proxyRequest(urlToProxyTo: String, body: String?, method: HttpMethod, request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String>{
        return fetch(urlToProxyTo, method, copyHeadersFromRequest(request), body).await()
    }
}


