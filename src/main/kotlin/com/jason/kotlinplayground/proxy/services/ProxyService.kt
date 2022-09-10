package com.jason.kotlinplayground.proxy.services

import com.jason.kotlinplayground.proxy.repositories.CachedResponseRepository
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import java.net.URI
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class ProxyService(private val cachedResponseRepository: CachedResponseRepository) {
    fun proxyRequest(urlToProxyTo: String, body: String?, method: HttpMethod, request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String>{
        val cachedResponses = cachedResponseRepository.findCachedResponses()
        val uri = URI(urlToProxyTo)
        val headers = createHeadersFromRequest(request)
        val httpEntity = HttpEntity(body, headers)
        val factory = BufferingClientHttpRequestFactory(SimpleClientHttpRequestFactory())
        val restTemplate = RestTemplate(factory)
        return try {
            val serverResponse = restTemplate.exchange(uri, method, httpEntity, String::class.java)
            response.setHeader(HttpHeaders.CONTENT_TYPE, serverResponse.headers[HttpHeaders.CONTENT_TYPE]?.firstOrNull())
            serverResponse
        }catch(e: HttpStatusCodeException){
            ResponseEntity.status(e.rawStatusCode)
                .headers(e.responseHeaders)
                .body(e.responseBodyAsString)
        }
    }
}

fun createHeadersFromRequest(request: HttpServletRequest) = HttpHeaders().also{ headers ->
    request.headerNames.toList().forEach{
        headers.set(it, request.getHeader(it))
    }
    headers.remove(HttpHeaders.ACCEPT_ENCODING)
}