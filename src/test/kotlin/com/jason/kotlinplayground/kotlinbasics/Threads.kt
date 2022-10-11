package com.jason.kotlinplayground.kotlinbasics

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.concurrent.thread

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

    @Test fun `thread`() = runBlocking {
        var count = 0;
        val untilCount = 10000;
        val threads = mutableListOf<Thread>()
        for (i in 1 until untilCount){
            val t = thread {
                count = count + 1
                Thread.sleep(10)
                // println("count is $count")
            }
            threads.add(t)
        }
        threads.forEach{ it.join() }
        println("final countdown: $count")
    }

    @Test fun `thread2`() = runBlocking {
        var count = 0;
        val untilCount = 10000;
        val executorService = Executors.newFixedThreadPool(untilCount)

        for (i in 1 until untilCount){
            executorService.execute{
                count = count + 1
                // println("count is $count")
            }
        }
        println("final countdown: $count")
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