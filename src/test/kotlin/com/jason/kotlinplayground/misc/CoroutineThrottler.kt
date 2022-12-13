package com.jason.kotlinplayground.misc

import com.jason.kotlinplayground.kotlin.CircuitBreaker
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

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
                        launch {
                            println("Start func")
                            func()
                            println("End func")
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
        fun makeHttpCallAndUpdateTheDatabase(i: Int){
            Thread.sleep(500)
            println("makeHttpCallAndUpdateTheDatabase $i")
            throw Exception("boooo")
        }

        for(i in 1..10){
            coroutineThrottler.register {
                cb.process {//swallow exceptions until N are reached, then blow up.
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