package com.jason.kotlinplayground.kotlin

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread


data class Counter(var count: Int){
    fun increment(){
        val temp = count
        count = temp + 1
    }
}
//https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-i-o.html
//https://stackoverflow.com/questions/55608138/how-could-i-know-what-the-number-of-threads-is-used-by-specific-coroutines-dispa
class Threads {
    @Test fun `dispatcher context`() = runBlocking {
        val results = mutableListOf<Deferred<Unit>>()
        for(i in 1 until 10){
            val deferred = async { defaultContextWork() }
            results.add(deferred)
        }
        results.forEach{
            it.await()
        }

        assert(1 == 1)
    }

    @Test fun `race condition demo - thread func`() {
        val counter = Counter(0)
        val untilCount = 1000000;
        val threads = mutableListOf<Thread>()
        for (i in 1 until untilCount){
            val t = thread {
                counter.increment()
                Thread.sleep(100)
            }
            threads.add(t)
        }
        threads.forEach{ it.join() }
        println("final countdown: ${counter.count}") //race condition results in sometimes being 999998, 999999
    }


    @Test fun `thread exceptions`(){
        val counter = Counter(0)
        val untilCount = 10;
        val threads = mutableListOf<Thread>()
        val atomicException: AtomicReference<Exception> = AtomicReference()
        for (i in 1 until untilCount){
            val t = thread {
                counter.increment()
                Thread.sleep(100)
                atomicException.set(CircuitBreakerTrippedException("boo"))
            }
            threads.add(t)
        }
        threads.forEach{ it.join() }
        val exception = atomicException.get()
        assert(exception is CircuitBreakerTrippedException)
    }

    @Test fun `race condition demo - executor service`() {
        var count = 0;
        val untilCount = 10000;
        val executorService = Executors.newFixedThreadPool(untilCount)
        val countDownLatch = CountDownLatch(untilCount - 1)
        for (i in 1 until untilCount){
            executorService.execute{
                val temp = count
                count = temp + 1
                Thread.sleep(100)
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
        println("final countdown: $count") //race condition results in sometimes being 9998, or 9999
    }
}

suspend fun defaultContextWork(){
    delay(3000L)
}

suspend fun ioContextWork() = withContext(Dispatchers.IO){
    delay(3000L)
}

fun getThreadCount(): Int{
    val threads = Thread.getAllStackTraces().keys.filter {
        println("thread name: ${it.name}")
        it.name.startsWith("CommonPool") || it.name.startsWith("ForkJoinPool")
    }
    return threads.size
}