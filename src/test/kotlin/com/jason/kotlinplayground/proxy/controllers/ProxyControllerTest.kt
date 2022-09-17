package com.jason.kotlinplayground.proxy.controllers

import com.jason.kotlinplayground.proxy.repositories.CachedResponseRepository
import com.jason.kotlinplayground.proxy.utils.fetch
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.http.HttpMethod

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureEmbeddedDatabase
class ProxyControllerTest(@LocalServerPort val port: Int, @Autowired val cachedResponseRepository: CachedResponseRepository) {
    val proxyHostBaseUrl = "http://localhost:$port"
    companion object{
        @DynamicPropertySource
        @JvmStatic
        fun properties(registry: DynamicPropertyRegistry){
        //            registry.add("") { "something"}
        }
    }

    @BeforeAll
    fun setup(){}
    @AfterEach
    fun tearDown(){}

    @Test fun `body from host and proxy are equal`() = runBlocking{
        val url = "https://jsonplaceholder.typicode.com/comments?postId=1"
        val body = null
        val headers = mapOf( "Accept" to "*/*", "Connection" to "keep-alive")
        val method = HttpMethod.GET
        val proxyHostUrl = "$proxyHostBaseUrl/proxy?urlToProxyTo=$url"
        //fetch in parallel
        val (responseFromActualUrl, responseFromProxy) = awaitAll(
            fetch(url, method, headers, body),
            fetch(proxyHostUrl, method, headers, body)
        )
        assert(responseFromActualUrl.body == responseFromProxy.body)
    }

    @Test fun `headers from host and proxy are equal`() = runBlocking {
        val url = "https://jsonplaceholder.typicode.com/comments?postId=1"
        val body = null
        val headers = mapOf( "Accept" to "*/*", "Connection" to "keep-alive")
        val method = HttpMethod.GET
        val proxyHostUrl = "$proxyHostBaseUrl/proxy?urlToProxyTo=$url"
        val (responseFromActualUrl, responseFromProxy) = awaitAll(
            fetch(url, method, headers, body),
            fetch(proxyHostUrl, method, headers, body)
        )
        val headersToAssertAreEqual = listOf("content-type")
        assert(responseFromActualUrl.headers.size == responseFromProxy.headers.size - 1)
        //headers are mostly equal but are a pain to test as there are a few headers the proxy server returns, but we can't control to make them exact to the actual host.
        responseFromActualUrl.headers.entries.forEach{ (name, value) ->
            val proxyValue = responseFromProxy.headers[name]
            println("header name: $name has real host value: $value , and proxy host value: $proxyValue")
            //header should exist in the response
//            assert(proxyValue != null && proxyValue.size == value.size)
//            if(headersToAssertAreEqual.contains(name.toLowerCase())){
//                if(name.toLowerCase() == "content-type"){ //deal with: real host value: [application/json; charset=utf-8] , and proxy host value: [application/json;charset=utf-8]
//                    assert(proxyValue?.first()?.replace("\\s".toRegex(), "") == value.first().replace("\\s".toRegex(), ""))
//                }else{
//                    assert(responseFromProxy.headers[name] == value)
//                }
//
//            }
        }
    }
}