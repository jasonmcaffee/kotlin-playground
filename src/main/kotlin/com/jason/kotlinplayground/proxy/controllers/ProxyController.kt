package com.jason.kotlinplayground.proxy.controllers

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class ProxyController {
    @RequestMapping("/proxy")
    fun proxyRequest(
        @RequestBody(required = false) body: String,
        @RequestParam urlToProxyTo: String,
        method: HttpMethod,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<String>{
        println("domain is: $urlToProxyTo, method is: $method, body is: $body")
        return ResponseEntity("tacos", HttpStatus.OK)
    }
}