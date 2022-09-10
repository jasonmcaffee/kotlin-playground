package com.jason.kotlinplayground.proxy.controllers

import com.jason.kotlinplayground.proxy.repositories.CachedResponseRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProxyControllerTest(@LocalServerPort val port: Int, @Autowired val cachedResponseRepository: CachedResponseRepository) {
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

    @Test
    fun doStuff(){
        val responses = cachedResponseRepository.findCachedResponses()
        assert(1 == 1)
    }
}