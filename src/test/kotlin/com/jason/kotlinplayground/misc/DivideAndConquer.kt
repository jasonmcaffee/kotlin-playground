package com.jason.kotlinplayground.misc

import com.jason.kotlinplayground.kotlin.CircuitBreaker
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
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

                nextBatch.forEach{func ->
                    val t = thread {
                        func()
                    }
                    threads.add(t)
                }

                threads.forEach{ it.join() }
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
            divideAndConquer.run()
        }catch(e: Exception){
            println("exception: ${e.message}")
        }
    }
}