package com.jason.kotlinplayground.misc

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.testcontainers.shaded.okhttp3.OkHttpClient
import org.testcontainers.shaded.okhttp3.Request
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

/**
 * Explore different approaches to running things in parallel.
 *
 * References:
 * https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-i-o.html
 * https://www.baeldung.com/kotlin/coroutines-waiting-for-multiple-threads
 */
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

    /**
     * Get the next N funcs to be ran from the list of registered funcs
     */
    private fun getNextBatch(): MutableList<() -> Unit> {
        val nextEndIndex = if(funcList.size < maxCallsPerSecond) funcList.size else maxCallsPerSecond
        val nextBatch = funcList.subList(0, nextEndIndex)
        return nextBatch
    }

    /**
     * Remove boilerplate into this function so that runUsingThreads, etc can just focus on implementing their strategy.
     * Each strategy is responsible for calling nextBatch.clear when they have processed it ("processed" may have different
     * meanings, depending on the strategy, but most of the time it means each item in the list has started running)
     */
    private fun processAll(strategy: (nextBatch: MutableList<() -> Unit>)-> Unit){
        while (funcList.size > 0) {
            val nextBatch = getNextBatch()
            timeMilli { getMilli ->
                runBlocking {//so we can call delay(ms)
                    strategy(nextBatch)
                    slowDownIfNeeded(getMilli())
                }
            }
        }
    }

    /**
     * Throttle if needed by delaying so that we at least take N seconds to complete one batch.
     */
    private suspend fun slowDownIfNeeded(msDurationOfLastBatch: Long){
        val timeDiff = 1000 - msDurationOfLastBatch
        if(timeDiff > 0 && funcList.size > 0){
            println("sleeping for $timeDiff ms")
            delay(timeDiff)
        }
    }

    /**
     * Use the traditional thread approach.
     * Since exceptions that occur in threads don't bubble up, we're left having to use an atomicReference of a possible
     * exception, and raise it up after the threads have finished processing.
     */
    fun runUsingThreadsJoin(){
        processAll { nextBatch ->
            val threads = mutableListOf<Thread>()
            val atomicException = AtomicReference<Exception>(null)//track whether a thread threw an exception
            nextBatch.forEach{func ->
                threads.add(thread {
                    try{
                        func()
                    }catch (e: Exception){
                        atomicException.set(e)
                    }
                })
            }
            nextBatch.clear() //remove batch from the original list, regardless if they fail
            threads.forEach{ it.join() }//wait for all threads to finish.
            //throw the last exception encountered in the thread so that it bubbles up the stack.
            if(atomicException.get() != null){
                throw atomicException.get()
            }
        }
    }

    /**
     * Uses an executor service to create and manage threads.
     * TODO: This probably isn't the most efficient approach.  Alternatively, keep N threads alive in the threadpool and use a countdown latch
     * to await processing. i.e. move executorService outside of this function and don't shut it down.
     * https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html
     *
     */
    fun runUsingExecutorServiceThreadPool(){
        processAll { nextBatch ->
            val executorService = Executors.newFixedThreadPool(nextBatch.size)
            val atomicException = AtomicReference<Exception>(null)//track whether a thread threw an exception
            nextBatch.forEach{func ->
                executorService.execute{
                    try{
                        func()
                    }catch(e: Exception){
                        atomicException.set(e)
                    }
                }
            }
            nextBatch.clear() //remove batch from the original list, regardless if they fail
            executorService.shutdown()
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
            //throw the last exception encountered in the thread so that it bubbles up the stack.
            if(atomicException.get() != null){
                throw atomicException.get()
            }
        }
    }

    /**
     * From a syntax perspective, this approach isn't all that great, but it is an opportunity to see some of the
     * underlying plumbing.
     */
    fun runUsingAsyncWithContext(){
        processAll { nextBatch ->
            runBlocking {//so we use runBlocking's coroutineScope.coroutineContext to await all asyncs.
                withContext(Dispatchers.IO){//no need for awaitAll with this approach.
                    nextBatch.forEach{ func ->
                        async {//you must specify the context here or it will be done synchronously for network calls.
                            func()
                        }
                    }
                    nextBatch.clear() //remove batch from the original list, regardless if they fail
                }//all async are completed by this point
            }
        }
    }

    /**
     * Alternative approach to asyncWithContext.  We can just keep a list of deferreds and await them all to complete.
     */
    fun runUsingAsyncAwaitAll(){
        processAll { nextBatch ->
            runBlocking {//async needs a coroutine scope
                val deferreds = mutableListOf<Deferred<*>>()
                nextBatch.forEach{ func ->
                    deferreds.add(async(Dispatchers.IO) {
                        func()
                    })
                }
                nextBatch.clear() //remove batch from the original list, regardless if they fail
                deferreds.awaitAll()
            }
        }
    }
}

class DivideAndConquerTests{
    fun doWork(i: Int, workName: String){
        timeMilli {getMilli ->
            println("start $workName $i")
            makeHttpRequest()
            println("end $workName $i complete in ${getMilli()} ms")
        }
    }
    @Test fun `should divide and conquer using threads strategy`(){
        val divideAndConquer = DivideAndConquer(2)
        for(i in 1..4){
            divideAndConquer.register { doWork(i, "threads") }
        }
//        start threads 1
//        start threads 2
//        end threads 2 complete in 393 ms
//        end threads 1 complete in 393 ms
//        sleeping for 580 ms
//        start threads 3
//        start threads 4
//        end threads 3 complete in 231 ms
//        end threads 4 complete in 236 ms
//        task threads completed in 1241 ms
        printTime("threads") { divideAndConquer.runUsingThreadsJoin() }
    }

    @Test fun `should divide and conquer using executor service strategy`(){
        val divideAndConquer = DivideAndConquer(2)

        for(i in 1..4){
            divideAndConquer.register { doWork(i, "executor") }
        }

//        start executor 2
//        start executor 1
//        end executor 1 complete in 423 ms
//        end executor 2 complete in 422 ms
//        sleeping for 552 ms
//        start executor 3
//        start executor 4
//        end executor 3 complete in 243 ms
//        end executor 4 complete in 250 ms
//        task executor completed in 1259 ms
        printTime("executor"){ divideAndConquer.runUsingExecutorServiceThreadPool() }
    }

    @Test fun `should divide and conquer using async with context strategy`(){
        val divideAndConquer = DivideAndConquer(2)
        for(i in 1..4){
            divideAndConquer.register { doWork(i, "asyncWithContext") }
        }

//        start asyncWithContext 2
//        start asyncWithContext 1
//        end asyncWithContext 1 complete in 389 ms
//        end asyncWithContext 2 complete in 387 ms
//        sleeping for 581 ms
//        start asyncWithContext 3
//        start asyncWithContext 4
//        end asyncWithContext 4 complete in 234 ms
//        end asyncWithContext 3 complete in 248 ms
//        task asyncWithContext completed in 1258 ms
        printTime("asyncWithContext"){ divideAndConquer.runUsingAsyncWithContext() }
    }

    @Test fun `should divide and conquer using async with await all strategy`(){
        val divideAndConquer = DivideAndConquer(2)
        for(i in 1..4){
            divideAndConquer.register { doWork(i, "asyncAwaitAll") }
        }

//        start asyncAwaitAll 1
//        start asyncAwaitAll 2
//        end asyncAwaitAll 2 complete in 449 ms
//        end asyncAwaitAll 1 complete in 459 ms
//        sleeping for 514 ms
//        start asyncAwaitAll 3
//        start asyncAwaitAll 4
//        end asyncAwaitAll 3 complete in 247 ms
//        end asyncAwaitAll 4 complete in 252 ms
//        task asyncAwaitAll completed in 1255 ms
        printTime("asyncAwaitAll") { divideAndConquer.runUsingAsyncAwaitAll() }
    }
}

fun printTime(taskName: String, func: ()-> Unit){
    timeMilli {getMilli ->
        func()
        println("task $taskName completed in ${getMilli()} ms")
    }
}

fun makeHttpRequest(){ //this takes about 450 ms
    timeMilli { getMilli ->
        try{
            OkHttpClient()
                .newCall(Request.Builder()
                    .url("https://www.google.com")
                    .build()
                ).execute()
//            println("got response in ${getMilli()} ms ${response.body()}")
        }catch (e:Exception){
            println("got exception in ${getMilli()} ms ${e.message}")
        }
    }
}