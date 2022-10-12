package com.jason.kotlinplayground.kotlin

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class DesignPatterns {
    @Test fun `singleton`(){
        //singletons should be lazy (ie only instantiated when needed for the first time)
        //singletons should be thread safe
        //singletons should be performant.

    }

    //https://www.baeldung.com/kotlin/lazy-initialization
    @Test fun `lazy initialization`(){
        val numberOfInitCalls = AtomicInteger()
        data class SlowToInit(val data: String){
            init {
                numberOfInitCalls.incrementAndGet()
                Thread.sleep(1000)
            }
        }
        //lazy (Lazy<T>) doesn't get evaluated until it is first referenced.
        //you can use lazy {...} if you want access to the Lazy delegate, which has isInitialized property.
        val lazyValue by lazy {  SlowToInit("some data") }
        val executorService = Executors.newFixedThreadPool(2)
        executorService.submit{ println(lazyValue) }
        executorService.submit{ println(lazyValue) }
        executorService.awaitTermination(5, TimeUnit.SECONDS)
        assert(numberOfInitCalls.get() == 1)

    }

    // @Test fun `lazy map`() = runBlocking{
    //     val numberOfInitCalls = AtomicInteger()
    //     val map = mutableMapOf<String, Lazy<String>>()
    //
    //
    //     suspend fun callApiToGetValue(key: String): String{
    //         Thread.sleep(100)
    //         return "$key - value"
    //     }
    //
    //     suspend fun getValue(key: String): String?{
    //         map[key] = lazy {
    //             numberOfInitCalls.incrementAndGet()
    //             runBlocking { callApiToGetValue(key)  }
    //         }
    //         return map[key]?.value
    //     }
    //
    //     getValue("key1")
    //     getValue("key1")
    //     getValue("key1")
    //     assert(numberOfInitCalls.get() == 1)
    // }
}