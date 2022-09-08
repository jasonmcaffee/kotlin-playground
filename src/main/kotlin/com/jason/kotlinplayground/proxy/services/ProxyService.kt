package com.jason.kotlinplayground.proxy.services

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

//https://blogs.ashrithgn.com/writing-a-reverse-proxy-service-in-spring-boot/
@Service
class ProxyService {
    fun proxyRequest(urlToProxyTo: String, body: String, method: HttpMethod, request: HttpServletRequest, response: HttpServletResponse){
        val uri = request.requestURI
        val headers = HttpHeaders()
        request.headerNames.toList().forEach {
            headers.set(it, request.getHeader(it))
        }
        val headerMap = request.headerNames.toList().map { it to request.getHeader(it) }
        // val multiValueMap: MultiValueMap<String, String> = ArrayListValuedHashMap<String, String>()
        // HttpHeaders(headerMap)

    }
}