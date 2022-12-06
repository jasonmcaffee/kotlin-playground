package com.jason.kotlinplayground.kotlin

import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.event.EventListener
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Service

data class EventData(val name: String)
class SomeEvent(source: Any, val data: EventData) : ApplicationEvent(source){}

@Service
class SomeService(){
    @EventListener
    fun handleApplicationEvent(e: SomeEvent){
        println("got event: ${e.data.name}")
        throw Exception("No")
    }
}
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureEmbeddedDatabase
class SpringEventPublisher(
    @Autowired val applicationEventPublisher: ApplicationEventPublisher,
    @Autowired val someService: SomeService
) {
    companion object Config {
        @Bean
        fun applicationEventPublisher(): ApplicationEventPublisher{
            val ctx = GenericApplicationContext()
            ctx.refresh()
            return ctx
        }

        @Bean
        fun someService(): SomeService{
            return SomeService()
        }
    }

    @Test fun `publish throws if listener throws`(){
        try{
            applicationEventPublisher.publishEvent(SomeEvent(this, EventData("jason")))
        }catch(e: Exception){
            println("publish exception: ${e.message}")
        }
        assert(1 == 1)
    }
}