package com.jason.kotlinplayground.kotlin

import org.junit.jupiter.api.Test
import org.springframework.util.StopWatch

/*
Primitive throttler to be used for single thread throttling.
 */
class ThreadThrottler(
    val maxCallsPerSecond: Int
){
    private val desiredExecutionTimeMs = 1000 / maxCallsPerSecond
    var isCurrentlyThrottled = false //for testing
    fun process(func: () -> Unit){
        val stopWatch = StopWatch()
        stopWatch.start()
        try{
            func()
        } finally{
            stopWatch.stop()
            val durationMs = stopWatch.totalTimeMillis
            val timeDiffMs = desiredExecutionTimeMs - durationMs
            if(timeDiffMs > 0){
                isCurrentlyThrottled = true
                Thread.sleep(timeDiffMs)
                isCurrentlyThrottled = false
            }
        }
    }
}

class ThreadThrottlerTests {

    @Test
    fun `should throttle N calls per second by slowing down processes via thread sleep`() {
        val throttler = ThreadThrottler(4)
        val stopWatch = StopWatch()
        stopWatch.start()
        for(i in 1..4){
            throttler.process {
                val math = 1 + 1
            }
        }
        stopWatch.stop()
        assert(stopWatch.totalTimeMillis >= 1000 && stopWatch.totalTimeMillis < 1100) //give a little wiggle room
    }


    @Test
    fun `should not throttle when processing is organically slow`(){
        val throttler = ThreadThrottler(4)
        val stopWatch = StopWatch()
        stopWatch.start()
        for(i in 1..4){
            throttler.process {
                val math = 1 + 1
                Thread.sleep(300)
            }
        }
        stopWatch.stop()
        assert(stopWatch.totalTimeMillis >= 1200 && stopWatch.totalTimeMillis < 1300) //give a little wiggle room
    }
}