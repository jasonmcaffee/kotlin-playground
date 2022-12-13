package com.jason.kotlinplayground.misc

import com.jason.kotlinplayground.kotlin.CircuitBreaker
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
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
    fun runUsingAsync(){
        val nextEndIndex = if(funcList.size - 1 < maxCallsPerSecond) funcList.size - 1 else maxCallsPerSecond
        val nextBatch = funcList.subList(0, nextEndIndex)
        if(nextBatch.isEmpty()){ return }

        timeMilli { getMilli ->
            runBlocking {//use runblocking here so that we don't need to do the entire call chain as suspend functions
                withContext(coroutineContext){//no need for awaitAll with this approach.
                    nextBatch.forEach{ func ->
                        async(Dispatchers.IO) {//you must specify the context here or it will be done synchronously.
                            println("running func...")
                            func()
                            println("done running func.")
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
    }
}

//alternative approach that works.
//                val deferreds = mutableListOf<Deferred<*>>()
//
//                nextBatch.forEach{ func ->
//                    val deferred = GlobalScope.async {
//                        println("running func...")
//                        func()
//                        println("done running func.")
//                    }
//                    deferreds.add(deferred)
//                }
//                deferreds.awaitAll()

class DivideAndConquerTests{
    @Test fun `should divide and conquer`(){
        val cb = CircuitBreaker(8)
        val divideAndConquer = DivideAndConquer(4)
        fun makeHttpCallAndUpdateTheDatabase(i: Int){
            println("start makeHttpCallAndUpdateTheDatabase $i")
            Thread.sleep(500)
            println("done makeHttpCallAndUpdateTheDatabase $i")
            // throw Exception("boooo")
        }

        for(i in 1..10){
            divideAndConquer.register {
                makeHttpCallAndUpdateTheDatabase(i)
            }
        }
        try{
            divideAndConquer.runUsingAsync()
        }catch(e: Exception){
            println("exception: ${e.message}")
        }
    }
}