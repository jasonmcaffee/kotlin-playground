package com.jason.kotlinplayground.misc

import com.jason.kotlinplayground.kotlin.CircuitBreaker
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.testcontainers.shaded.okhttp3.OkHttpClient
import org.testcontainers.shaded.okhttp3.Request

class CoroutineThrottler(
    val maxCallsPerSecond: Int
){
    private val funcList = mutableListOf<suspend ()->Unit>()
    /**
     * ensure that the passed in function takes at least 1000 / maxCallsPerSecond to complete.
     */
    fun register(func: suspend () -> Unit){
       funcList.add(func)
    }

    fun run(){
        val nextEndIndex = if(funcList.size - 1 < maxCallsPerSecond) funcList.size - 1 else maxCallsPerSecond
        val nextBatch = funcList.subList(0, nextEndIndex)
        if(nextBatch.isEmpty()){ return }

        timeMilli { getMilli ->
            runBlocking {
                coroutineScope {
                    nextBatch.forEach{func ->
                        launch(Dispatchers.IO) {
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
        run()
    }
}


class CoroutineThrottlerTests {

    @Test fun `should slow`() = runBlocking{
        val cb = CircuitBreaker(8)
        val coroutineThrottler = CoroutineThrottler(4)
        suspend fun makeHttpCallAndUpdateTheDatabase(i: Int){
            println("makeHttpCallAndUpdateTheDatabase $i")
//            delay(500) //this does not force things to be synchronized. ie. all coroutines fire at the same time.

            //This proves that http calls force things to be synchronized.
//            makeHttpCallAndUpdateTheDatabase 1
//            got response in 325 ms org.testcontainers.shaded.okhttp3.internal.http.RealResponseBody@274872f8
//            done with makeHttpCallAndUpdateTheDatabase 1
//            makeHttpCallAndUpdateTheDatabase 2
//            got response in 170 ms org.testcontainers.shaded.okhttp3.internal.http.RealResponseBody@6c345c5f
//            done with makeHttpCallAndUpdateTheDatabase 2
//            makeHttpCallAndUpdateTheDatabase 3
//            got response in 156 ms org.testcontainers.shaded.okhttp3.internal.http.RealResponseBody@4c36250e
//            done with makeHttpCallAndUpdateTheDatabase 3
            makeHttpRequest2()
            println("done with makeHttpCallAndUpdateTheDatabase $i")
//            throw Exception("boooo")
        }

        for(i in 1..10){
            coroutineThrottler.register {
                cb.processSuspend {//swallow exceptions until N are reached, then blow up.
                    makeHttpCallAndUpdateTheDatabase(i)
                }
            }
        }
        try{
            coroutineThrottler.run()
        }catch(e: Exception){
            println("exception: ${e.message}")
        }

    }
}

fun makeHttpRequest2(){ //this takes about 450 ms
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