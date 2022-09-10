package com.jason.kotlinplayground.proxy.controllers

import com.jason.kotlinplayground.proxy.repositories.CachedResponseRepository
import com.jason.kotlinplayground.proxy.utils.fetch
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

    @Test fun doStuff() = runBlocking{
        val responses = cachedResponseRepository.findCachedResponses()

        val url = "https://api.github.com/users/hadley/orgs"
        val body = null
        val headers = mapOf(
            "Accept" to "*/*",
            //"Accept-Encoding" to "gzip, deflate, br", //<-- don't do this or requestFromActualUrl will not get decoded correctly.
            "Connection" to "keep-alive"
        )
        val method = HttpMethod.GET
        val responseFromActualUrl = fetch(url, method, headers, body).await()
        val proxyHostUrl = "$proxyHostBaseUrl/proxy?urlToProxyTo=$url"
        val responseFromProxy = fetch(proxyHostUrl, method, headers, body).await()
        assert(responseFromActualUrl.body == responseFromProxy.body)
    }
}