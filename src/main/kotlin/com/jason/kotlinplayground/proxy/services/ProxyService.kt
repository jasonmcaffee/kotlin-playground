package com.jason.kotlinplayground.proxy.services

import com.jason.kotlinplayground.proxy.repositories.CachedResponseRepository
import com.jason.kotlinplayground.proxy.utils.copyHeadersFromRequest
import com.jason.kotlinplayground.proxy.utils.fetch
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class ProxyService(private val cachedResponseRepository: CachedResponseRepository) {
    fun proxyRequest(urlToProxyTo: String, body: String?, method: HttpMethod, request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String>{
        //copy headers
        val headers = copyHeadersFromRequest(request)
        return fetch(urlToProxyTo, method, headers, body).also { serverResponse ->
            //have our response match the content type of the response
            response.setHeader(HttpHeaders.CONTENT_TYPE, serverResponse.headers[HttpHeaders.CONTENT_TYPE]?.firstOrNull())
        }
    }
}


