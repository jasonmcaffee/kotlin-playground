package com.jason.kotlinplayground.kotlin

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.util.StopWatch

/*
Primitive throttler to be used for single thread throttling.
 */
class ThreadThrottler(
    val maxCallsPerSecond: Int
){
    private var currentCallCount = 0
    private val stopWatch = StopWatch()
    private val log = LoggerFactory.getLogger(ThreadThrottler::class.java)
    /**
     * ensure that the passed in function takes at least 1000 / maxCallsPerSecond to complete.
     */
    fun run(func: () -> Unit){
        if(!stopWatch.isRunning){
            stopWatch.start()
        }

        currentCallCount++
        try{
            func()
        } finally{
            if(currentCallCount >= maxCallsPerSecond){
                stopWatch.stop()
                val durationMs = stopWatch.totalTimeMillis
                val timeDiffMs = 1000 - durationMs
                if(timeDiffMs > 0){
                    currentCallCount = 0
//                    log.info("Throttling for $timeDiffMs ms") // we probably dont want to log this.
                    Thread.sleep(timeDiffMs)
                }
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
            throttler.run {
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
            throttler.run {
                val math = 1 + 1
                Thread.sleep(300)
            }
        }
        stopWatch.stop()
        assert(stopWatch.totalTimeMillis >= 1200 && stopWatch.totalTimeMillis < 1300) //give a little wiggle room
    }
}