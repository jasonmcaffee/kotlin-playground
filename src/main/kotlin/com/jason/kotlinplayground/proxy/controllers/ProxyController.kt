package com.jason.kotlinplayground.proxy.controllers

import com.jason.kotlinplayground.proxy.services.ProxyService
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class ProxyController(val proxyService: ProxyService) {
    @RequestMapping("/proxy")
    suspend fun proxyRequest(
        @RequestBody(required = false) body: String?,
        @RequestParam urlToProxyTo: String,
        method: HttpMethod,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<String> =
        proxyService.proxyRequest(urlToProxyTo, body, method, request, response)
}