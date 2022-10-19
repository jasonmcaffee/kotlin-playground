package com.jason.kotlinplayground.kotlin

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
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
        val threadCount = 2
        val countDownLatch = CountDownLatch(threadCount)
        val executorService = Executors.newFixedThreadPool(threadCount)
        executorService.submit{ println(lazyValue); countDownLatch.countDown() }
        executorService.submit{ println(lazyValue); countDownLatch.countDown() }
        executorService.awaitTermination(5, TimeUnit.SECONDS)
        countDownLatch.await()
        assert(numberOfInitCalls.get() == 1)
    }

}