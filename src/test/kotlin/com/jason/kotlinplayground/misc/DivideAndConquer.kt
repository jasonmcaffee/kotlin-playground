package com.jason.kotlinplayground.misc

import com.jason.kotlinplayground.kotlin.CircuitBreaker
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.testcontainers.shaded.okhttp3.OkHttpClient
import org.testcontainers.shaded.okhttp3.Request
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

class DivideAndConquer(
    val maxCallsPerSecond: Int
){
    private val funcList = mutableListOf<()->Unit>()
    /**
     * ensure that the passed in function takes at least 1000 / maxCallsPerSecond to complete.
     */
    fun register(func: () -> Unit){
        funcList.add(func)
    }

    fun run(){
        val nextEndIndex = if(funcList.size - 1 < maxCallsPerSecond) funcList.size - 1 else maxCallsPerSecond
        val nextBatch = funcList.subList(0, nextEndIndex)
        if(nextBatch.isEmpty()){ return }

        timeMilli { getMilli ->
            runBlocking {
                val threads = mutableListOf<Thread>()

                val atomicException = AtomicReference<Exception>(null)
                nextBatch.forEach{func ->
                    val t = thread {
                        try{
                            func()
                        }catch (e: Exception){
                            atomicException.set(e)
                        }

                    }
                    threads.add(t)
                }

                threads.forEach{ it.join() }
                nextBatch.clear() //remove the items from the original list
                if(atomicException.get() != null){
                    throw atomicException.get()
                }
                val timeDiff = 1000 - getMilli()
                if(timeDiff > 0 && funcList.size > 0){
                    println("sleeping for $timeDiff ms")
                    delay(timeDiff)
                }
            }
        }
        run()
    }

    //https://www.baeldung.com/kotlin/coroutines-waiting-for-multiple-threads
    fun runUsingAsyncOne(){
        val nextEndIndex = if(funcList.size - 1 < maxCallsPerSecond) funcList.size - 1 else maxCallsPerSecond
        val nextBatch = funcList.subList(0, nextEndIndex)
        if(nextBatch.isEmpty()){ return }

        timeMilli { getMilli ->
            runBlocking {//use runblocking here so that we don't need to do the entire call chain as suspend functions
                withContext(coroutineContext){//no need for awaitAll with this approach.
                    nextBatch.forEach{ func ->
                        async(Dispatchers.IO) {//you must specify the context here or it will be done synchronously.
                            func()
                        }
                    }
                }
                nextBatch.clear() //remove the items from the original list
                val timeDiff = 1000 - getMilli()
                if(timeDiff > 0 && funcList.size > 0){
                    println("sleeping for $timeDiff ms")
                    delay(timeDiff)
                }
            }
        }
        runUsingAsyncOne()
    }
    fun runUsingAsyncTwo(){
        val nextEndIndex = if(funcList.size < maxCallsPerSecond) funcList.size else maxCallsPerSecond
        val nextBatch = funcList.subList(0, nextEndIndex)
        println("funcList size: ${funcList.size}")
        if(nextBatch.isEmpty()){ return }

        timeMilli { getMilli ->
            runBlocking {//use runblocking here so that we don't need to do the entire call chain as suspend functions
                val deferreds = mutableListOf<Deferred<*>>()

                nextBatch.forEach{ func ->
                    val deferred = async(Dispatchers.IO) {
                        func()
                    }
                    deferreds.add(deferred)
                }
                deferreds.awaitAll()
                nextBatch.clear() //remove the items from the original list
                val timeDiff = 1000 - getMilli()
                if(timeDiff > 0 && funcList.size > 0){
                    println("sleeping for $timeDiff ms")
                    delay(timeDiff)
                }
            }
        }
        runUsingAsyncTwo()
    }
}

class DivideAndConquerTests{
    @Test fun `should divide and conquer`(){
        val cb = CircuitBreaker(2)
        val divideAndConquer = DivideAndConquer(4)
        fun makeHttpCallAndUpdateTheDatabase(i: Int){
            cb.process {
                println("start makeHttpCallAndUpdateTheDatabase $i")
                if(i >= 3){
                    throw Exception("boo")
                }
                makeHttpRequest()
                println("done makeHttpCallAndUpdateTheDatabase $i")
            }

        }

        for(i in 1..10){
            divideAndConquer.register {
                makeHttpCallAndUpdateTheDatabase(i)
            }
        }
        try{
//            start makeHttpCallAndUpdateTheDatabase 1
//            start makeHttpCallAndUpdateTheDatabase 3
//            start makeHttpCallAndUpdateTheDatabase 2
//            start makeHttpCallAndUpdateTheDatabase 4
//            done makeHttpCallAndUpdateTheDatabase 1
//            done makeHttpCallAndUpdateTheDatabase 2
//            exception here: Circuit breaker encountered 2 exceptions, exceeding max of 2
//            divideAndConquer.runUsingAsyncOne()

//            start makeHttpCallAndUpdateTheDatabase 1
//            start makeHttpCallAndUpdateTheDatabase 2
//            start makeHttpCallAndUpdateTheDatabase 3
//            start makeHttpCallAndUpdateTheDatabase 4
//            done makeHttpCallAndUpdateTheDatabase 2
//            done makeHttpCallAndUpdateTheDatabase 1
//            exception here: Circuit breaker encountered 2 exceptions, exceeding max of 2
            divideAndConquer.runUsingAsyncTwo()
        }catch(e: Exception){
            println("exception here: ${e.message}")
        }
    }
}

fun makeHttpRequest(){ //this takes about 450 ms
    timeMilli { getMilli ->
        val response = try{
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://www.nasa.gov/sites/default/files/thumbnails/image/curiosity_selfie.jpg")
                .build()
            val call = client.newCall(request)
            val response = call.execute()
            println("got response in ${getMilli()} ms ${response.body()}")
        }catch (e:Exception){
            println("got exception in ${getMilli()} ms ${e.message}")
        }
    }
}